#!/bin/bash
# 자정 크래시 재현 및 메모리/커넥션 풀 테스트 스크립트
#
# 사용법:
#   1. docker compose -f docker-compose-test.yml up --build -d
#   2. 서버 기동 대기 (약 30초)
#   3. ./test-memory.sh
#
# 테스트 시나리오:
#   Phase 1: 기본 상태 확인
#   Phase 2: 동시 요청 부하 테스트 (자정 트래픽 시뮬레이션)
#   Phase 3: 결과 수집

BASE_URL="http://localhost:8080"
ACTUATOR_URL="${BASE_URL}/manage"

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo "============================================"
echo "  자정 크래시 재현 및 메모리 테스트"
echo "============================================"
echo ""

# 서버 상태 확인
echo -n "서버 상태 확인... "
for i in $(seq 1 30); do
    if curl -s "${ACTUATOR_URL}/health" > /dev/null 2>&1; then
        echo -e "${GREEN}OK${NC}"
        break
    fi
    if [ $i -eq 30 ]; then
        echo -e "${RED}FAIL - 서버가 응답하지 않습니다${NC}"
        exit 1
    fi
    sleep 2
done

# HikariCP 메트릭 수집 함수
collect_metrics() {
    local label=$1
    echo ""
    echo -e "${YELLOW}=== ${label} ===${NC}"

    # Docker 컨테이너 메모리
    docker stats loatodo-test --no-stream --format "  컨테이너 메모리: {{.MemUsage}} ({{.MemPerc}})" 2>/dev/null

    # HikariCP 메트릭 (Prometheus)
    local metrics=$(curl -s "${ACTUATOR_URL}/prometheus" 2>/dev/null)
    if [ -n "$metrics" ]; then
        echo "$metrics" | grep "hikaricp_connections_active " | awk '{printf "  활성 커넥션: %s\n", $2}'
        echo "$metrics" | grep "hikaricp_connections_idle " | awk '{printf "  유휴 커넥션: %s\n", $2}'
        echo "$metrics" | grep "hikaricp_connections_pending " | awk '{printf "  대기 커넥션: %s\n", $2}'
        echo "$metrics" | grep "hikaricp_connections_max " | awk '{printf "  최대 커넥션: %s\n", $2}'
        echo "$metrics" | grep "hikaricp_connections_timeout_total " | awk '{printf "  타임아웃 횟수: %s\n", $2}'

        # JVM 메모리
        local heap_used=$(echo "$metrics" | grep 'jvm_memory_used_bytes{area="heap"' | awk '{sum+=$2} END {printf "%.1fMB", sum/1024/1024}')
        local heap_max=$(echo "$metrics" | grep 'jvm_memory_max_bytes{area="heap"' | awk '{sum+=$2} END {printf "%.1fMB", sum/1024/1024}')
        local nonheap_used=$(echo "$metrics" | grep 'jvm_memory_used_bytes{area="nonheap"' | awk '{sum+=$2} END {printf "%.1fMB", sum/1024/1024}')
        local threads=$(echo "$metrics" | grep "jvm_threads_live_threads " | awk '{printf "%d", $2}')
        echo "  JVM Heap: ${heap_used} / ${heap_max}"
        echo "  JVM Non-Heap: ${nonheap_used}"
        echo "  활성 스레드: ${threads}"
    fi
}

# ============ Phase 1: 기본 상태 ============
echo ""
echo "============================================"
echo "  Phase 1: 기본 상태 측정"
echo "============================================"
collect_metrics "초기 상태"

# ============ Phase 2: 부하 테스트 ============
echo ""
echo "============================================"
echo "  Phase 2: 동시 요청 부하 테스트"
echo "============================================"
echo "  (자정 시나리오: 동시 요청 50개 x 3라운드)"
echo ""

# 동시 요청 발생 함수
send_concurrent_requests() {
    local count=$1
    local round=$2
    echo -e "  라운드 ${round}: ${count}개 동시 요청 전송 중..."

    for i in $(seq 1 $count); do
        # 다양한 API 엔드포인트에 분산 요청
        case $((i % 5)) in
            0) curl -s -o /dev/null -w "" "${BASE_URL}/api/v1/character-list" -H "Authorization: Bearer test" & ;;
            1) curl -s -o /dev/null -w "" "${BASE_URL}/api/v1/member" -H "Authorization: Bearer test" & ;;
            2) curl -s -o /dev/null -w "" "${BASE_URL}/api/v1/notification/status" -H "Authorization: Bearer test" & ;;
            3) curl -s -o /dev/null -w "" "${BASE_URL}/api/v1/friend" -H "Authorization: Bearer test" & ;;
            4) curl -s -o /dev/null -w "" "${BASE_URL}/api/v1/cube/statistics" -H "Authorization: Bearer test" & ;;
        esac
    done

    # 모든 백그라운드 프로세스 대기
    wait
    echo "  라운드 ${round} 완료"
}

# 라운드 1
send_concurrent_requests 50 1
collect_metrics "라운드 1 직후"

sleep 3

# 라운드 2
send_concurrent_requests 50 2
collect_metrics "라운드 2 직후"

sleep 3

# 라운드 3 (가장 강한 부하)
send_concurrent_requests 100 3
collect_metrics "라운드 3 직후 (100개 동시 요청)"

# ============ Phase 3: 회복 확인 ============
echo ""
echo "============================================"
echo "  Phase 3: 회복 확인 (30초 대기)"
echo "============================================"
sleep 30
collect_metrics "30초 후 회복 상태"

# 서버 생존 확인
echo ""
echo -n "서버 생존 확인... "
if curl -s "${ACTUATOR_URL}/health" | grep -q "UP"; then
    echo -e "${GREEN}서버 정상 동작 중${NC}"
else
    echo -e "${RED}서버 다운됨!${NC}"
fi

echo ""
echo "============================================"
echo "  테스트 완료"
echo "============================================"
echo ""
echo "정리: docker compose -f docker-compose-test.yml down"
