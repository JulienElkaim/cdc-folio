# FOL.io 
### _...What If the Terminal was the best UI to track your money?!_

## How to Use
_____________

In your IDE, please go in `folio-app` module and run the main method on **`FolioApp`** class.  
<br>
![portfolio_example.png](assets%2Fportfolio_example.png)

## Play with the Setup
_______________________

**Portfolio CSV**: Find it in the folio-app's `resources/data` folder.  
**Use another Portfolio**: use the property `portfolio.csv.resource.path` in `application.properties` file. CSV Portfolio should be in the resources folder.  
**H2 Database**: Find it in the folio-app's `resources/data` folder.    
**Feed new Data on App Start**: Use the `DatabaseFeeder` class provided to input data in DB, and support new securities.   
**Market Pulse**: change the frequency of Market update received in folio-app's `application.properties` file. `market.pulsar.frequency.ms`  
**Portfolio Updates**: change the refresh rate of your positions in the console in folio-app's `application.properties` file.  `portfolio.refresh.rate.ms`  
**Risk Free Rate**: change the risk free rate in folio-app's `application.properties` file.  `reference.risk.free.rate`  
**Starting Prices**: mocked in the Ser

## Understand the Code Structure
________________________________

I structured the code as a multi-module project to show-case the separation of concerns I usually apply in my projects.   
You can have a more complex example in a personal project I played with last year https://github.com/maukaim/Bulo-Project/tree/main/back-end.
The idea is separation of concerns. I usually prefer to have a nette separation betwee Domain/Busines logic layers and the Technical/Application layer.
Therefore, I tend to have "-app", "-io" , '-domain', '-core', '-serialization' '-models' etc modules.   


In this showcase project, we don't have marshalling or I/O needs. The main separation is -app for Spring boot Vs -core for domain code.  
Other modules are to represent either bounding elements (Could be external ! Vendors? Other teams?) like market data, reference data or pricing.


Outside folio-app, classes are agnostic of Spring Boot usage. This, in theory, allow for simpler Framework switching.  
In reality, the class structure itself is tied to the initial assumptions of the framework used (What if we move to Akka?!)  
We handle **the Dependency Injection** in `ServiceBeansConfig`, `UtilBeansConfig` and `StoreBeansConfig`.


## Tradeoffs & Shortcuts for Time Constraint
_______________________________________

- No time to ensure the db file provided is compatible on every OS, so I provided an easy way to **feed data at start** to ensure anyone running the App have them handy.

- I **abstract the question of File System** and path representation by limiting input data (portfolio) from the resources folder, so it becomes **relative to the classpath itself**.

- The **Starting Spot Prices** for the reference time are mocked directly in the **DI config file**. I provide a default price as well, so it virtually supports Spot pricing for all securities, given other details are in DB. 

- Java provides bad support for **NullPointer** management (not as newer JVM based like Kotlin). To avoid extending the exercise time, I did not checked for null values when data comes from another `core module` component, but **cared when comes from external sources**, i.e ref-data.

- I did **polymorphism** for the Definition classes at the **Service level**. However, I would prefer do it at DB level for real project, depending on the DBMS used.  

- I used **Gradle** multiple times for Single module projects. As I wanted to showcase **multi-modules** structuration, I saved time by working with Maven and then translated to a Gradle app. I am ok transferring to Gradle for future work projects.

- Java 8 misses **Collections initialization helpers** (i.e List.of, Map.of etc). To go fast, I accepted to **sacrifice readability** and init with static blocks following initialization.  


## Can We Extend the Features?
______________________________

#### **Support another Security Type pricing?**  
With the Manager pattern used in `PriceEngineImpl`, it is the single point of contact for pricing API and it's quite easy to support pricing of a new security.  

#### **Publish The Portfolio elsewhere?**   
As we separated the actual PortfolioState computation and publication, it's easy to plug a new `Publisher`, and diversify the media knowing about our current positions.  

#### **Change any part of the application's Behavior?**  
With the high usage of Interfaces to perform Dependency Inversion, it's easy to change a component Behavior.
