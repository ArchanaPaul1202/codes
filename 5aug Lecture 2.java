package com.company;
import java.util.*;
import java.util.ArrayList;
import java.util.stream.Stream;

class Employee{
    String name, city;
    Integer salary;
    Employee(String n, String c, Integer s){
        name=n;
        salary=s;
        city=c;
    }
}
public class StreamDemo{
    public static void main(String [] args){
        ArrayList<Employee> al=new ArrayList<>();
        al.add(new Employee("Ravi" , "Kanpur", 50));
        al.add(new Employee("Arvind" , "Lucknow", 55));
        al.add(new Employee("Ataulla" , "Gaya", 45));
        al.add(new Employee("Sudha" , "Bathinda", 40));
        al.add(new Employee("Roshan" , "Kanpur", 52));

        Stream<Employee> str=al.stream();
//        str = str.sorted( (a, b) -> a.name.compareTo(b.name));

        //Print names of employee who belongs to Kanpur in sorted order of their names
        str=str.filter((e)->e.equals("Kanpur") && e.salary>50000).sorted((a,b)->a.name.compareTo(b.name));
        str.forEach((e)->{System.out.println(e.name);
        });

    }
}