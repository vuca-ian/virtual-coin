package cn.virtual.coin.server.configuration;

import cn.virtual.coin.broker.htx.utils.ServiceException;
import jakarta.annotation.Resource;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Set;

/**
 * @author gdyang
 * @since  2021/7/30 10:37 上午
 */
@Slf4j
@Component
public final class ScheduleRegistrar implements ResourceLoaderAware, EnvironmentAware, InitializingBean {
    private static final String QUARTZ_JOB_PACKAGES =  "${quartz.job.package}";
    private static final String QUARTZ_JOB_ENABLED = "${quartz.job.enabled}";
    private Environment environment;
    private ResourceLoader resourceLoader;
    @Resource
    private Scheduler scheduler;

    @SuppressWarnings("unchecked")
    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("start schedule......");
        String enabled = resolve(QUARTZ_JOB_ENABLED);
        if(!Boolean.parseBoolean(enabled)){
            return;
        }
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(this.resourceLoader);
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(JobTask.class);
        scanner.addIncludeFilter(annotationTypeFilter);
        Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(resolve(QUARTZ_JOB_PACKAGES));
        for (BeanDefinition candidateComponent : candidateComponents) {
            if (candidateComponent instanceof AnnotatedBeanDefinition) {
                AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
                AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
                Map<String, Object> attributes = annotationMetadata.getAnnotationAttributes(JobTask.class.getCanonicalName());
                try {
                    if (attributes != null) {
                        startJobInstance((Class<QuartzJobBean>) Class.forName(beanDefinition.getBeanClassName()), attributes);
                    }
                } catch (ClassNotFoundException e) {
                    throw new ServiceException("e", e);
                }
            }
        }
    }

    public void startJobInstance(Class<QuartzJobBean> target,Map<String, Object> attributes) throws Exception{
        JobDetail detail = JobBuilder.newJob(target).withIdentity(resolve((String) attributes.get("name"))).storeDurably().build();
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(resolve((String) attributes.get("cron"))).withMisfireHandlingInstructionDoNothing();
        String condition = attributes.containsKey("condition") ? attributes.get("condition").toString(): null;
        String havingValue = attributes.containsKey("havingValue") ? attributes.get("havingValue").toString() : null;
        if(StringUtils.hasText(condition)){
            String conditionValue = resolve(condition);
            if (!StringUtils.hasText(conditionValue)){
                return;
            }
            if(StringUtils.hasText(havingValue)){
                if(!havingValue.equals(conditionValue)){
                    return;
                }
            }
        }
        JobDataMap jobData = new JobDataMap();
        jobData.put("name", attributes.get("name"));
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(resolve((String) attributes.get("name")), "group").forJob(detail).usingJobData(jobData).withSchedule(scheduleBuilder).build();
        log.info("start job[{}], cron: {}", attributes.get("name"),  attributes.get("cron"));
        scheduler.scheduleJob(detail, trigger);
    }

    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    private ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(@NonNull AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }

    private String resolve(String value) {
        if (StringUtils.hasText(value)) {
            return this.environment.resolvePlaceholders(value);
        }
        return value;
    }
}
