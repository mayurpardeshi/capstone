## What is SpringBoot?
`Springboot` is not a framework - it is configuration and bootstrap layer on top of Spring framework

It does below in nutshell:
* Assembles Spring components
* Applies conditional configurations
* Boots an embedded runtime and exposes sane defaults
* SpringBoot automates 80% of boring Spring

## SpringBoot boot sequence:
    `@SpringBootApplication
    public class App {
        public static void main(String[] args) {
            SpringApplication.run(App.class, args);
        }
    }
    `
Everything starts here:
#### 1. SpringApplication.run() - The real entry point
This method does 7 important things:
1. Create Spring application - Determines application type - Servlet, reactive, None - loads application context accordingly
2. Load ApplicationContextInitializers - These modify the context **before beans are created** Use case: Property binding, Environment setup, Profile activation
3. Load ApplicationListeners - Examples: ApplicationStartingEvent, ApplicationEnvironmentPreparedEvent, ApplicationReadyEvent. This is Spring boot's event driven lifecycle
4. Prepare environment - Boot merges properties from > CommandLine args > System properties > OS environment files > application.yml > application.properties > Default properties
5. Create ApplicationContext - Depending on the classpath it creates and Boot does the classpath inspection here
6. Bean definition phase(Auto-Configuration Happens) - The heart of Spring boot. Spring loads AutoConfiguration classes
7. Refresh Context - App is LIVE - `applicationContext.refresh();` At this point : beans created, tomcat started, dispatcher servlet is registered, app is listening to a port


## Classpath = Behavior
If class path contains:
**_Classpath               Boot does_**
spring-webmvc           Creates a `dispatcherServlet`
spring-data-jpa         Creates EntityManager
h2                      Auto configures in-memory Db
spring-security         Secures everything

## SpringBoot != Spring MVC
Common misconception:
**Spring                          SpringBoot**
IoC container                       Bootstrapper
DependencyInjection                 Opinionated defaults
MVC framework                       Auto-configuration
Bean lifecycle                      Packaging & runtime

SpringBoot uses Spring MVC and not replace it.


### How to master SpringBoot
#### Step 1: Master Core Spring
* Bean Lifecycle
* ApplicationContext
* Proxies (JDK vs CGLIB)
* Transaction boundaries
If we don't understand proxies , we don't understand Spring

#### Step 2: Understand how Boot thinks?
Read Auto-configuration code
Read about - DataSourceAutoConfiguration, WebMvcAutoConfiguration, JacksonAutoConfiguration
It will also help to override correctly

#### Step 3: Use --debug
java -jar app.jar --debug - This prints positive and negative matches - we understand why a bean is created

#### Step 4: Learn Conditions Deeply
Boot is just if else - we need to know @ConditionalOnClass, @ConditionalOnMissingBean, @ConditionalOnProperty

#### Step 4: Understand Actuators internals\
Actuator exposes:
* Environment
* Beans
* Mappings
* Health
It allows us to di-sect a SpringBoot application at runtime

