## What?
Dispatcher Servlet is a request orchestrator and NOT a handler, This does not process any business logic.
Lets think of it as deterministic state machine for any Http requests

## Where DispatcherServlet sits?
Client
   |
Servlet container (Jetty or Tomcat)
    |
DispatcherServlet  (Spring MVC starts here)
    |
Spring managed components


#### Key points:
1. Servlet containers knows nothing about the container
2. Spring knows nothing about its sockets
3. Dispatcher Servlets is the bridge



## 2. How Dispatcher Servlet is born?

`DispatcherServlet dispatcherServlet = new DispatcherServlet(applicationContext)` -> This is auto registered at `/`
which means, every Http requests hits its it (unless filtered earlier)

## 3. Internal Components:
DispatcherServlet delegates everything to strategy interfaces
Like : 
**Responsibility              Interface**
1. Find controller -         `HandlerMapping`
2. Call controller -         `HandlerAdapter`
3. Convert a request -       `HttpMessageConverter`
4. HandleExceptions -        `HandlerExceptionResolver`
5. Render Response -         `ViewResolver`


## What happens when we get POST /user/123 and contentType: application/json 

1. doDispatch() - This is core method `protected void doDispatch(HttpServletRequest request, HttpServletResponse response)`
2. find the controller - `HandlerExecutionChain chain = handlerMapping.getHandler(request);`
        Typical mapping : `RequestMappingHandlerMapping` matches : HTTP method, path, header, consumes/ produces
3. Picking the correct adapter: Dispatcher servlet do NOT call the controller directly, but uses :
        For `@RestController` -> RequestMappingHandlerAdapter (why adapter -> because : old controller(Controller Interface, Annotated controllers, future extensions))
4. Argument Resolution - Before calling your method `public User create(@PathVariable Long id, @RequestBody User user, Principal principal)`
        Spring would resolve : 
             @PathVariable -> URL template
             @RequestBody -> HttpMessageConverter
            Principal -> SecurityContext
5. Now finally controller method execution - `adapter.handle(request, response, handler)` == then our business logic runs
            Return types: User, ResponseEntity<User>, void, ModelAndView
6. Return value handling: 
     If return type is `@RestController` Then HttpMessageConverter -> Jackson -> JSON -> Writes directly to response body
    If return type is `@Controller` then viewName -> `ViewResolver`(ThymeLeaf/JSP/etc)
7. Exception Handling: `throw new IllegalArgumentException();` If controller throws:
        Dispatcher Servlet invokes: `HandlerExceptionResolver` for `throw new IllegalArgumentException();`
   In order:
     ExceptionHandlerExceptionResolver (@ExceptionHandler)
     ResponseStatusExceptionResolver
     DefaultHandlerExceptionResolver


## Why DispatcherServlet?
Everything here is:
1. Interface driven
2. Replaceable
3. Deterministic

We can add custom `HandlerMapping`, override `HttpMessageConverter`, plug new `HandlerExceptionResolver` -- clean architecture

## Dispatcher Servlet vs Filter vs Interceptor
**Filter (Servlet specific)**:
> Runs before Spring
> No controller context
> Example: Logging, CORS

**Interceptor (Spring)**:
> Runs inside DispatcherServlet
> Knows handler and models
> Example: Auth checks, metrics

_Filter_
↓
_DispatcherServlet_
↓
_Interceptor_ (preHandle)
↓
_Controller_
↓
_Interceptor_ (postHandle)
↓
_View_



## Why Spring needs Dispatcher servlet?
Without it : 
* Every controller would need servlet plumbing
* No unified Exception handling would be possible
* No argument resolution would be possible
* Non content negotiations

Dispatcher Servlet centralises:
* Cross-cutting concerns
* Control flow
* Protocol translation

        
   
        