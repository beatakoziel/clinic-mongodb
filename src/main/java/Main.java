
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import models.Department;
import models.Doctor;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Aggregates.count;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class Main {

    public static void main(String[] args) throws IOException {
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoClient mongoClient = new MongoClient("localhost", MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build());
        MongoDatabase database = mongoClient.getDatabase("clinic")
                .withCodecRegistry(pojoCodecRegistry);

        database.getCollection("doctors").drop();
        database.getCollection("departments").drop();
        database.createCollection("doctors");
        database.createCollection("departments");
        MongoCollection<Doctor> doctorsMap = database.getCollection("doctors", Doctor.class);
        MongoCollection<Department> departmentsMap = database.getCollection("departments", Department.class);
        while (true) {
            Integer choice = printMenu();
            clearScreen();
            System.out.println(choice);
            if (choice > 0 && choice < 9) {
                switch (choice) {
                    case 1:
                        addElementToDatabase(doctorsMap, departmentsMap);
                        break;
                    case 2:
                        editElement(doctorsMap, departmentsMap);
                        break;
                    case 3:
                        getElementById(doctorsMap, departmentsMap);
                        break;
                    case 4:
                        getAll(doctorsMap, departmentsMap);
                        break;
                    case 5:
                        removeElement(doctorsMap, departmentsMap);
                        break;
                    case 6:
                        setSalary(doctorsMap);
                        break;
                    case 7:
                        getElementByName(doctorsMap, departmentsMap);
                        break;
                }
                System.out.println("Press enter to continue...");
                System.in.read();
            } else System.out.println("Wrong number, choose again.");
        }
    }

    private static void getAll(MongoCollection<Doctor> doctors, MongoCollection<Department> departments) throws IOException {
        System.out.println("Getting all values");
        Integer s = printSubMenu();
        if (s > 0 && s < 3) {
            switch (s) {
                case 1:
                    Block<Doctor> doctorsPrintBlock = System.out::println;
                    doctors.find().forEach(doctorsPrintBlock);
                    break;
                case 2:
                    Block<Department> departmentsPrintBlock = System.out::println;
                    departments.find().forEach(departmentsPrintBlock);
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

    private static void getElementById(MongoCollection<Doctor> doctors, MongoCollection<Department> departments) throws IOException {
        System.out.println("Getting by id");
        Integer s = printSubMenu();
        Scanner scanner = new Scanner(System.in);
        if (s > 0 && s < 3) {
            System.out.println("Write id:");
            switch (s) {
                case 1:
                    String playerId = scanner.next();
                    Block<Doctor> doctorsPrintBlock = System.out::println;
                    doctors.find(eq("_id", new ObjectId(playerId))).forEach(doctorsPrintBlock);
                    break;
                case 2:
                    String departmentId = scanner.next();
                    Block<Department> departmentsPrintBlock = System.out::println;
                    departments.find(eq("_id", new ObjectId(departmentId))).forEach(departmentsPrintBlock);
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

    private static void getElementByName(MongoCollection<Doctor> doctors, MongoCollection<Department> departments) throws IOException {
        System.out.println("Getting by name");
        Integer s = printSubMenu();
        Scanner scanner = new Scanner(System.in);
        if (s > 0 && s < 3) {
            System.out.println("Write name:");
            switch (s) {
                case 1:
                    String playerName = scanner.next();
                    Block<Doctor> doctorsPrintBlock = System.out::println;
                    doctors.find(eq("firstname", playerName)).forEach(doctorsPrintBlock);
                    break;
                case 2:
                    String departmentName = scanner.next();
                    Block<Department> departmentsPrintBlock = System.out::println;
                    departments.find(eq("name", departmentName)).forEach(departmentsPrintBlock);
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

    private static void editElement(MongoCollection<Doctor> doctors, MongoCollection<Department> departments) {
        System.out.println("Editing");
        Integer s = printSubMenu();
        Scanner scanner = new Scanner(System.in);
        if (s > 0 && s < 3) {
            System.out.println("Write id:");
            switch (s) {
                case 1:
                    String playerId = scanner.next();
                    if (doctors.count(eq("_id", new ObjectId(playerId))) != 0) {
                        Doctor doctor = getDoctorFromUser(departments, scanner);
                        doctors.updateOne(eq("_id", new ObjectId(playerId)), combine(
                                set("firstname", doctor.getFirstname()),
                                set("surname", doctor.getSurname()),
                                set("salary", doctor.getSalary()),
                                set("department", doctor.getDepartment())));
                        System.out.println(playerId + " => " + doctor);
                    } else System.out.printf("Doctor with id %s not found.%n", playerId);
                    break;
                case 2:
                    String departmentId = scanner.next();
                    if (departments.count(eq("_id", new ObjectId(departmentId))) != 0) {
                        Department department = getSportDepartment(scanner);
                        doctors.updateOne(eq("_id", new ObjectId(departmentId)), combine(
                                set("name", department.getName()),
                                set("creationYear", department.getCreationYear())));
                        System.out.println(departmentId + " => " + department);
                    } else System.out.printf("Department with id %s not found.%n", departmentId);
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

    private static void removeElement(MongoCollection<Doctor> doctors, MongoCollection<Department> departments) {
        System.out.println("Removing");
        Integer s = printSubMenu();
        Scanner scanner = new Scanner(System.in);
        if (s > 0 && s < 3) {
            System.out.println("Write id:");
            switch (s) {
                case 1:
                    String playerId = scanner.next();
                    if (doctors.count(eq("_id", new ObjectId(playerId))) != 0) {
                        doctors.deleteOne(eq("_id", new ObjectId(playerId)));
                    } else System.out.printf("Doctor with id %s not found.%n", playerId);
                    break;
                case 2:
                    String departmentId = scanner.next();
                    if (departments.count(eq("_id", new ObjectId(departmentId))) != 0) {
                        departments.deleteOne(eq("_id", new ObjectId(departmentId)));
                    } else System.out.printf("Department with id %s not found.%n", departmentId);
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

    private final static Pattern UUID_REGEX_PATTERN =
            Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");

    private static void addElementToDatabase(MongoCollection<Doctor> doctors, MongoCollection<Department> departments) {
        System.out.println("Adding to database");
        Integer s = printSubMenu();
        Scanner scanner = new Scanner(System.in);
        if (s > 0 && s < 3) {
            switch (s) {
                case 1:
                    Doctor doctor = getDoctorFromUser(departments, scanner);
                    doctors.insertOne(doctor);
                    break;
                case 2:
                    Department department = getSportDepartment(scanner);
                    departments.insertOne(department);
                    break;
            }
        } else System.out.println("Wrong number, choose again.");
    }

    private static void setSalary(MongoCollection<Doctor> doctors) {
        System.out.println("Calculate average salary");
        doctors.updateMany(eq("surname", "Nowak"), set("salary", 10000));
    }

    private static Department getSportDepartment(Scanner scanner) {
        System.out.println("Write department name:");
        String name = scanner.next();
        System.out.println("Write creation year:");
        Integer creationYear = scanner.nextInt();
        return Department.builder()
                .name(name)
                .creationYear(creationYear)
                .build();
    }

    private static Doctor getDoctorFromUser(MongoCollection<Department> departments, Scanner scanner) {
        System.out.println("Write doctor first name:");
        String firstname = scanner.next();
        System.out.println("Write doctor surname:");
        String surname = scanner.next();
        System.out.println("Write doctor salary:");
        Integer doctorSalary = scanner.nextInt();
        Department department = null;
        return Doctor.builder()
                .firstname(firstname)
                .surname(surname)
                .department(department)
                .salary(doctorSalary)
                .build();
    }

    private static Integer printMenu() {
        System.out.println("\nCLINIC - MONGODB");
        System.out.println("\nChoose operation:");
        System.out.println("1.ADD");
        System.out.println("2.EDIT");
        System.out.println("3.GET BY ID");
        System.out.println("4.GET ALL");
        System.out.println("5.REMOVE");
        System.out.println("6.GIVE BIGGER SALARY");
        System.out.println("7.GET BY NAME");
        Scanner scan = new Scanner(System.in);
        return scan.nextInt();
    }

    private static Integer printSubMenu() {
        System.out.println("\nChoose table:");
        System.out.println("1.DOCTORS");
        System.out.println("2.DEPARTMENTS");
        Scanner scan = new Scanner(System.in);
        return scan.nextInt();
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
