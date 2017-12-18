package bcs.testing.test

import org.springframework.stereotype.Service

// A service we can use to route to,
// making code look nice other than some ugly
// function within the controller
@Service
class HelloService {
    fun getHello() = "Hello service!"
}
