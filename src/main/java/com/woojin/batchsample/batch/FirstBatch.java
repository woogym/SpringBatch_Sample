package com.woojin.batchsample.batch;

import com.woojin.batchsample.entity.AfterEntity;
import com.woojin.batchsample.entity.BeforeEntity;
import com.woojin.batchsample.repository.AfterRepository;
import com.woojin.batchsample.repository.BeforeRepository;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class FirstBatch {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager platformTransactionManager;

    private final BeforeRepository beforeRepository;
    private final AfterRepository afterRepository;

    @Bean
    public Job firstJob() {
        System.out.println("first job");

        return new JobBuilder("firstJob(해당 Job에 대한 이름을 인자로 받는다)", jobRepository)
                .start(firstStep()) // start -> 이 작업에서 첫번쨰로 시작할 step을 지정한다.
//                .next() step이 2개 이상이다 하면 next를 이용하여 계속해서 step을 추가할 수 있다.
                .build(); // 마무리
    }

    // step
    @Bean
    public Step firstStep() {
        System.out.println("first step");

        return new StepBuilder("firstStep", jobRepository)
                // 10개(chunk 명시값) 단위로 읽기 -> 처리 -> 쓰기가 진행된다
                // 청크가 진행되다가 실패했을 경우,
                // 트랜잭션 매니저가 다시 처리(롤백, 재시도, 등등)할 수 있도록,
                // spring batch가 알아서 수행한다.
                .<BeforeEntity, AfterEntity> chunk(10, platformTransactionManager)
                .reader(beforeReader())
                .processor(middleProcessor())
                .writer(afterWriter())
                .build();
    }

    // 읽기
    @Bean
    public RepositoryItemReader<BeforeEntity> beforeReader() {

        return new RepositoryItemReaderBuilder<BeforeEntity>()
                .name("beforeReader")
                .pageSize(10)
                .methodName("findAll")
                .repository(beforeRepository)
                .sorts(Map.of("id", Direction.ASC))
                .build();
    }

    // 처리
    @Bean
    public ItemProcessor<BeforeEntity, AfterEntity> middleProcessor() {

        return new ItemProcessor<BeforeEntity, AfterEntity>() {
            @Override
            public AfterEntity process(final BeforeEntity item) throws Exception {

                AfterEntity afterEntity = new AfterEntity();
                afterEntity.setUserName(item.getUserName());

                return afterEntity;
            }
        };
    }

    // 쓰기
    @Bean
    public RepositoryItemWriter<AfterEntity> afterWriter() {

        return new RepositoryItemWriterBuilder<AfterEntity>()
                .repository(afterRepository)
                .methodName("save")
                .build();
    }
}
