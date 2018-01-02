package spring.pry.springbootrestful.model.Service.controller;


import java.util.Hashtable;

import org.springframework.web.bind.annotation.PathVariable;
import spring.pry.springbootrestful.model.Service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.pry.springbootrestful.model.person;



@RestController
@RequestMapping("persons")
public class personcontroller {

    @Autowired
    PersonService ps;

    @GetMapping (value="/all", produces="application/json")
    public Hashtable<String, person> getAll() {
        return ps.getAll();
    }

    @GetMapping ("{/id}")
    public person getPerson(@PathVariable("id") String id) {
        return ps.getPerson(id);
    }
}
