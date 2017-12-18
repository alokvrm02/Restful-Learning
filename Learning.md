# Learning

# Terminology

Spring IoC
- Inversion of Control
    - Instead of having a specific bit of code call general functions, IoC uses a general container which you then put specific functions inside of it.
    - 

Spring AOP
- Aspect Oriented Programming
    - Breaking down of programming into distinct aspects: Logging, Security, etc.
    - Functions connecting different aspects are called cross-cutting concerns.

Bean
- A bean is an object created/managed by the IoC container.
- Beans can have configurations applied to them.
- @Bean tells Spring that a function returns a value to be registered as a bean.

Dependency Injection
- It is the act of separating out functions/classes so that they are able to be run/tested independently from other parts of the system. Dependency Injection then glues it all together. Code relating to the functions are no longer in the main code, and instead handled by Spring which is able to use config files to construct the function.
- Can be seen where @Inject or @Autowired is used.

MVC
- Model View Controller
    - Model: Database
    - View: Html
    - Controller: Routing

