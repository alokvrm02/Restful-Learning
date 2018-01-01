package model.Service;

import java.util.Hashtable;

import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;
import model.person;

@Service
public class PersonService {
    Hashtable<String, person> persons = new Hashtable<String, person>();

    public PersonService() {
        person p = new person();
        p.setId("1");
        p.setFirstname("Priyanka");
        p.setLastname("Sharma");
        p.setage(27);
        person.put("1", p);


        p = new person();
        p.setId("2");
        p.setFirstname("Chirag");
        p.setLastname("Sharma");
        p.setAge(29);
        person.put("2", p);
    }

    public person getPerson(String id) {
        if (persons.containsKey(id))
            return persons.get(id);
        else
            return null;
    }

    public Hashtable<String, person> getAll() {
        return persons;
    }
}