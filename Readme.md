Steps :- 
mvn clean install 

# add in pom.xml :- 
    <dependency>
      <groupId>com.multimodule</groupId>
      <artifactId>mysql</artifactId>
      <version>1.0.0-rc1</version>
    </dependency>


This library helps one integrate jpa with multiple databases on multi module project.It creates beans of entityManagerFactory, 
platform transaction manager based on the application properties specified by the user . This project is module aware and helps
developer to use jpa in multi module project.

Developer just has to include this library , add few annotations , few properties and you are done .

#              Application Properties changes          

Here moduleName is identifier for different modules(can be single module too !) of spring boot application.shard refers to database in property
{moduleName}.jpa.shard.names :- define databases which can be accessed by a module .

example :-
# what all databases this module connects to, this needs to be specified here
module1.jpa.shard.names=db1

# datasource related properties , here db1 is databaase
module1.jpa.shard.db1.masters.datasource.url=jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=TRUE
module1.jpa.shard.db1.masters.datasource.username=root
module1.jpa.shard.db1.masters.datasource.driverClassName=org.h2.Driver
module1.jpa.shard.db1.masters.datasource.password=
module1.jpa.shard.db1.masters.datasource.initialSize=3
module1.jpa.shard.db1.masters.datasource.minIdle=3
module1.jpa.shard.db1.masters.datasource.testWhileIdle=true

# jpa integration related properties

module1.jpa.shard.db1.masters.jpa.packagesToScan=com.multimodule.mysql.entity 
module1.jpa.shard.db1.masters.jpa.repositorypackages=com.multimodule.mysql.repository

# jpa related properties, all properties provided by spring data library can be provided here 
module1.jpa.properties.shard.db1.masters.jpa.hibernate.hbm2ddl.auto=create
module1.jpa.properties.shard.db1.masters.jpa.hibernate.dialect=org.hibernate.dialect.MySQL5Dialect
module1.jpa.properties.shard.db1.masters.jpa.show-sql=true

#                     Spring boot main class changes          ####
In ApplicationConfig, Spring boot main application :-
exclude two classes from scanning :-  DataSourceTransactionManagerAutoConfiguration.class,
                                     		HibernateJpaAutoConfiguration.class
Example :-

@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class,
		HibernateJpaAutoConfiguration.class})



####                          How To Use                ######
In Dao class , to get a particular entity manager :-
This line gives entity manager of master of a particular modulename and database Name
@PersistenceContext(unitName = "{moduleName}-{shardName}-master-entity")
    private EntityManager entityManager;

Example :-
@PersistenceContext(unitName = "module1-db1-master-entity")
    private EntityManager entityManager;

To start a transaction of master of particular database in particular module:-

  @Transactional(transactionManager = "{moduleName}-{shardName}-master-transaction")

  example :-
  
  @Transactional(transactionManager = "module1-db1-master-transaction")
      public void performOperation(){

      }
      
Support for jpa repository has been added in library .
For this one needs to add property

# jpa repositories package scan  
module1.jpa.shard.db1.masters.jpa.repositorypackages=com.ie.naukri.repository
which is of format :- 
{module-name}.jpa.shard.{database-name}.masters.jpa.repositorypackages={repository-package-name}

Code remains same as that of syntax of spring-data-jpa documentation in this case.
There is not need to autowire entitymanager and invoke query for this case , and same
jpa repository pattern can be applied.Just for @Transactional, code remains same as 
earlier, we need to include transactional manager as per our requirement, 

## example jpa with transactional in case of multi-module project
    @Transactional(transactionManager = "module1-db1-master-transaction")
    public void  throwException(){
        TestEntity testEntity=new TestEntity();
        testEntity.setName("entity one entry");
        testRepository.save(testEntity);
        TestEntity2 testEntity1=new TestEntity2();
        testEntity1.setName("entity 2 entry");
        throw new RuntimeException();
    }

One can also refer test cases for implementation !
