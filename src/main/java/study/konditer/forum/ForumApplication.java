package study.konditer.forum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;

import study.konditer.forum.dto.AnswerInputDto;
import study.konditer.forum.dto.AnswerOutputDto;
import study.konditer.forum.dto.QuestionInputDto;
import study.konditer.forum.dto.QuestionOutputDto;
import study.konditer.forum.dto.ReactionInputDto;
import study.konditer.forum.dto.TagInputDto;
import study.konditer.forum.dto.TagOutputDto;
import study.konditer.forum.dto.UserInputDto;
import study.konditer.forum.dto.UserOutputDto;
import study.konditer.forum.model.Role;
import study.konditer.forum.model.User;
import study.konditer.forum.model.emun.UserRoles;
import study.konditer.forum.repository.RoleRepository;
import study.konditer.forum.repository.UserRepository;
import study.konditer.forum.service.AnswerService;
import study.konditer.forum.service.QuestionService;
import study.konditer.forum.service.ReactionService;
import study.konditer.forum.service.TagService;
import study.konditer.forum.service.UserService;

@SpringBootApplication
public class ForumApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForumApplication.class, args);
    }

    @Component
    public class Init implements CommandLineRunner {

        private final PasswordEncoder passwordEncoder;
        private final RoleRepository roleRepository;
        private final UserRepository userRepository;
        private final UserService userService;
        private final QuestionService questionService;
        private final TagService tagService;
        private final AnswerService answerService;
        private final ReactionService reactionService;

        public Init(
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository,
            UserRepository userRepository,
            UserService userService,
            QuestionService questionService,
            TagService tagService,
            AnswerService answerService,
            ReactionService reactionService
        ) {
            this.passwordEncoder = passwordEncoder;
            this.roleRepository = roleRepository;
            this.userRepository = userRepository;
            this.userService = userService;
            this.questionService = questionService;
            this.tagService = tagService;
            this.answerService = answerService;
            this.reactionService = reactionService;
        }

        @Override
        public void run(String... args) throws Exception {

            System.out.println("Starting datagen");

            int USERS_AMOUNT = 0;
            int QUESTIONS_AMOUNT = 20;
            int TAGS_AMOUNT = 10;
            int ANSWERS_AMOUNT = 100;
            int REACTIONS_AMOUNT = 200;
            int[] TAGS_PER_QUESTION_RANGE = {1, 5};

            Random random = new Random();
            Faker faker = new Faker();


            System.out.println("- Generating roles");
            roleRepository.save(new Role(UserRoles.ADMIN));
            roleRepository.save(new Role(UserRoles.USER));


            System.out.println("- Generating tags");
            Set<String> tagsNamesSet = new HashSet<>();
            for (int i = 0; i < TAGS_AMOUNT; i++) {
                tagsNamesSet.add(faker.lorem().word());
            }
            for (String tagName : tagsNamesSet) {
                tagService.add(new TagInputDto(tagName));
            }
            List<TagOutputDto> tags = tagService.getAll();


            System.out.println("- Generating users");
            userRepository.save(new User(
                "admin", 
                passwordEncoder.encode("admin"), 
                roleRepository.findRoleByName(UserRoles.ADMIN).get(), 
                LocalDateTime.now(), 
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                0,
                false
            ));
            userRepository.save(new User(
                "user", 
                passwordEncoder.encode("user"), 
                roleRepository.findRoleByName(UserRoles.USER).get(), 
                LocalDateTime.now(), 
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(),
                0,
                false
            ));
            for (int i = 0; i < USERS_AMOUNT; i++) {
                String passwd = faker.lorem().word();
                userService.add(new UserInputDto(
                        faker.name().fullName(),
                        passwd, passwd
                ));
            }
            List<UserOutputDto> users = userService.getAll();


            System.out.println("- Generating questions");
            for (int i = 0; i < QUESTIONS_AMOUNT; i++) {

                Set<Long> questionTags = new HashSet<>();
                int amount = TAGS_PER_QUESTION_RANGE[0] + 
						random.nextInt(TAGS_PER_QUESTION_RANGE[1] - TAGS_PER_QUESTION_RANGE[0]);

                for (int k = 0; k < amount; k++) {
					questionTags.add(tags.get(random.nextInt(tags.size())).id());
				}

				questionService.add(new QuestionInputDto(
						users.get(random.nextInt(users.size())).id(),
						faker.lorem().sentence(),
						String.join("\n", faker.lorem().paragraphs(random.nextInt(2, 4))),
						new ArrayList<>(questionTags)
				));
            }
            List<QuestionOutputDto> questions = questionService.getAll();


			System.out.println("- Generating answers");
			for (int i = 0; i < ANSWERS_AMOUNT; i++) {
				answerService.add(new AnswerInputDto(
					users.get(random.nextInt(users.size())).id(),
					questions.get(random.nextInt(questions.size())).id(),
					faker.lorem().paragraph()
				));
			}
            List<AnswerOutputDto> answers = answerService.getAll();


			System.out.println("- Generating reactions");
			for (int i = 0; i < REACTIONS_AMOUNT; i++) {
				reactionService.add(new ReactionInputDto(
                    users.get(random.nextInt(users.size())).id(), 
                    answers.get(random.nextInt(answers.size())).id(), 
                    random.nextBoolean()
                ));
			}

            System.out.println("Datagen done");
        }
    }
}
