package model.Service.controller;


import java.util.Hashtable;

import model.Service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import model.person;

public class personcontroller {

    PersonService ps;

    public Hashtable<String, person> getAll() {
        return ps.getAll();
    }

    public person getPerson(String id) {
        return ps.getPerson(id);
    }
}
