package lostark.todo.domain.character;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.NotNull;
import lostark.todo.domain.BaseTimeEntity;
import lostark.todo.domain.member.Member;
import org.json.simple.JSONObject;

import javax.persistence.*;

@Entity
@Table(name = "characters")
public class Character extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "characters_id")
    private long id;

    @NotNull
    private String serverName;

    @NotNull
    private String characterName;

    @NotNull
    private int characterLevel; //전투레벨

    @NotNull
    private String characterClassName; //캐릭터 클래스

    @NotNull
    private double itemLevel; //아이템레벨

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @JsonBackReference //순환참조 방지
    private Member member;

    @NotNull
    private boolean selected; //true면 출력할 캐릭(디폴트 true)

    @Embedded
    private CharacterDayContent characterDayContent;

    //초기 JSONObject로 만드는 생성자
    public Character(JSONObject jsonObject) {
        characterName = jsonObject.get("CharacterName").toString();
        characterLevel = Integer.parseInt(jsonObject.get("CharacterLevel").toString());
        characterClassName = jsonObject.get("CharacterClassName").toString();
        serverName = jsonObject.get("ServerName").toString();
        itemLevel = Double.parseDouble(jsonObject.get("ItemMaxLevel").toString().replace(",", ""));
        selected = true;
        characterDayContent = new CharacterDayContent(); //기본 생성자 (true, 0, 0, true, 0, 0)
    }

    protected Character() {
    }

    public Character changeSelected() {
        this.selected = !selected;
        return this;
    }

    public void changeItemLevel(double itemMaxLevel) {
        this.itemLevel = itemMaxLevel;
    }

    public long getId() {
        return this.id;
    }

    @NotNull
    public String getServerName() {
        return this.serverName;
    }

    @NotNull
    public String getCharacterName() {
        return this.characterName;
    }

    @NotNull
    public int getCharacterLevel() {
        return this.characterLevel;
    }

    @NotNull
    public String getCharacterClassName() {
        return this.characterClassName;
    }

    @NotNull
    public double getItemLevel() {
        return this.itemLevel;
    }

    public Member getMember() {
        return this.member;
    }

    @NotNull
    public boolean isSelected() {
        return this.selected;
    }

    public CharacterDayContent getCharacterDayContent() {
        return this.characterDayContent;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setServerName(@NotNull String serverName) {
        this.serverName = serverName;
    }

    public void setCharacterName(@NotNull String characterName) {
        this.characterName = characterName;
    }

    public void setCharacterLevel(@NotNull int characterLevel) {
        this.characterLevel = characterLevel;
    }

    public void setCharacterClassName(@NotNull String characterClassName) {
        this.characterClassName = characterClassName;
    }

    public void setItemLevel(@NotNull double itemLevel) {
        this.itemLevel = itemLevel;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public void setSelected(@NotNull boolean selected) {
        this.selected = selected;
    }

    public void setCharacterDayContent(CharacterDayContent characterDayContent) {
        this.characterDayContent = characterDayContent;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Character)) return false;
        final Character other = (Character) o;
        if (!other.canEqual((Object) this)) return false;
        if (this.getId() != other.getId()) return false;
        final Object this$serverName = this.getServerName();
        final Object other$serverName = other.getServerName();
        if (this$serverName == null ? other$serverName != null : !this$serverName.equals(other$serverName))
            return false;
        final Object this$characterName = this.getCharacterName();
        final Object other$characterName = other.getCharacterName();
        if (this$characterName == null ? other$characterName != null : !this$characterName.equals(other$characterName))
            return false;
        if (this.getCharacterLevel() != other.getCharacterLevel()) return false;
        final Object this$characterClassName = this.getCharacterClassName();
        final Object other$characterClassName = other.getCharacterClassName();
        if (this$characterClassName == null ? other$characterClassName != null : !this$characterClassName.equals(other$characterClassName))
            return false;
        if (Double.compare(this.getItemLevel(), other.getItemLevel()) != 0) return false;
        final Object this$member = this.getMember();
        final Object other$member = other.getMember();
        if (this$member == null ? other$member != null : !this$member.equals(other$member)) return false;
        if (this.isSelected() != other.isSelected()) return false;
        final Object this$characterDayContent = this.getCharacterDayContent();
        final Object other$characterDayContent = other.getCharacterDayContent();
        if (this$characterDayContent == null ? other$characterDayContent != null : !this$characterDayContent.equals(other$characterDayContent))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Character;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final long $id = this.getId();
        result = result * PRIME + (int) ($id >>> 32 ^ $id);
        final Object $serverName = this.getServerName();
        result = result * PRIME + ($serverName == null ? 43 : $serverName.hashCode());
        final Object $characterName = this.getCharacterName();
        result = result * PRIME + ($characterName == null ? 43 : $characterName.hashCode());
        result = result * PRIME + this.getCharacterLevel();
        final Object $characterClassName = this.getCharacterClassName();
        result = result * PRIME + ($characterClassName == null ? 43 : $characterClassName.hashCode());
        final long $itemLevel = Double.doubleToLongBits(this.getItemLevel());
        result = result * PRIME + (int) ($itemLevel >>> 32 ^ $itemLevel);
        final Object $member = this.getMember();
        result = result * PRIME + ($member == null ? 43 : $member.hashCode());
        result = result * PRIME + (this.isSelected() ? 79 : 97);
        final Object $characterDayContent = this.getCharacterDayContent();
        result = result * PRIME + ($characterDayContent == null ? 43 : $characterDayContent.hashCode());
        return result;
    }

    public String toString() {
        return "Character(id=" + this.getId() + ", serverName=" + this.getServerName() + ", characterName=" + this.getCharacterName() + ", characterLevel=" + this.getCharacterLevel() + ", characterClassName=" + this.getCharacterClassName() + ", itemLevel=" + this.getItemLevel() + ", member=" + this.getMember() + ", selected=" + this.isSelected() + ", characterDayContent=" + this.getCharacterDayContent() + ")";
    }
}
