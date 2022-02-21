Coverage: 100% completed system
# QA Inventory Management System

This project will create an inventory management system, that will allow the end user to engage with the system to recieve the following outcomes.

- Add a customer to the system
- View all customers in the system
- Update a customer in the system
- Delete a customer in the system.
- Add an item to the system
- View all items in the system
- Update an item in the system
- Delete an item in the system
- Create an order in the system.
- View all orders in the system.
- Delete an order in the system
- Add an item to an order.
- Calculate a cost for an order.
- Delete an item in an order

A Command Line Interface (CLI) withinn JAVA will react with a MySQL Database using a CRUD functionality to:

- CREATE
- READE
- UPDATE
- DELETE

## Getting Started

1. Download or fork file from this repository.
2. Run IMSPROJECT in JAVA Eclipse.
3. Navigate to db.properties > src/test/resources and change db.url to your MySQL url and set db.user password to your MySQl password, in order to connect your database to the program.
4. Navigate to Runner > src/main/java and run program
5. The console will then begin to run the CRUD functionality and you may start to use the application.

The following functionality will be shown if application has been succesfully executed:

```
Welcome to my QA Inventory Management System!
Which entity would you like to use?
CUSTOMER: Information about customers
ITEM: Individual Items
ORDER: Purchases of items
STOP: To close the application
```

### Prerequisites and Installation

The following programs are needed to be installed to allow further development and testing

To run this project it is recommended that you install the following programs to see full functionality of codes and relative databases.

```
- IDE ECLIPSE JAVA - instructions on how to complete installation - https://www.eclipse.org/downloads/packages/installer
- MAVEN - instructions on how to complete installation - https://o7planning.org/10101/install-maven-for-eclipse
- MySQL - instructions on how to complete installation - https://www.mysqltutorial.org/install-mysql/
- jUnit Test - instructions on how to complete installation - https://www.guru99.com/download-installation-junit.html
```

## Running the tests

To run tests on this application:

```
1. Naviagte to src/test/java 
2. Right click > Coverage as Junit Test
```

### Unit Tests 

The following tests have been created to test the item class, and will test if the coding has been implemented correctly to create an item name, value, delete iteam and read me functionalities.

```
public class ItemDAOTest {

	private final ItemDAO DAO = new ItemDAO();

	@Before
	public void setup() {
		DBUtils.connect();
		DBUtils.getInstance().init("src/test/resources/sql-schema.sql", "src/test/resources/sql-data.sql");
	}

	@Test
	public void testCreate() {
		final Item created = new Item(2L, "TV", 123.22);
		assertEquals(created, DAO.create(created));
	}

	@Test
	public void testReadAll() {
		List<Item> expected = new ArrayList<>();
		expected.add(new Item(1L, "iWatch", 188.88));
		assertEquals(expected, DAO.readAll());
	}

	@Test
	public void testReadLatest() {
		assertEquals(new Item(1L, "iWatch", 188.88), DAO.readLatest());
	}

	@Test
	public void testRead() {
		final long ID = 1L;
		assertEquals(new Item(1L, "iWatch", 188.88), DAO.read(ID));
	}

	@Test
	public void testUpdate() {
		final Item updated = new Item(1L, "iWatch", 288.88);
		assertEquals(updated, DAO.update(updated));
	}

	@Test
	public void testDelete() {
		assertEquals(1, DAO.delete(1));
	}
```


## Deployment

This application can be packaged in bytes and into .javac files for full running.

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Versioning

We use [SemVer](http://semver.org/) for versioning.

## Authors

Omar Zaib Mahmood

## License

This project is licensed under QA Training Academy - all works and codes are owned by QA Academy.

## Acknowledgments

The author would like to acknowledge the QA Training team for all its efforts and helps, aswell as the team members within the cohort who leading up to the project were helpful and inspiring. Moreover the author would like to thank family and friends for being supportive throughout this course, and god almighty.
