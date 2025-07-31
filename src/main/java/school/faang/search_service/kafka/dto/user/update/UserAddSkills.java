package school.faang.search_service.kafka.dto.user.update;

import school.faang.search_service.document.skill.SkillFilter;

import java.util.List;

public record UserAddSkills(
        long id,
        List<SkillFilter> skills
) implements UserUpdateEvent {

    @Override
    public String getType() {
        return "USER_ADD_SKILLS";
    }

    @Override
    public long getId() {
        return id;
    }
}
