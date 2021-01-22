package pt.isec.pd2021.demo.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isec.pd2021.demo.AuthorizationFilter;
import pt.isec.pd2021.demo.Server.RMI.ServerRMIInterface;
import pt.isec.pd2021.demo.models.User;

import java.rmi.RemoteException;
import java.sql.SQLException;

@RestController
@RequestMapping("user")
public class UserController {
    ServerRMIInterface server;

    public UserController(ServerRMIInterface server) {
        this.server = server;
    }

    @PostMapping("/login")
    public User login(@RequestBody User user) throws RemoteException, SQLException {

        String aux = user.hashCode() + " " + user.getUsername();
        server.registoCliente("", user.getUsername(), user.getPassword(), null);
        AuthorizationFilter.tokens.add(aux);

        user.setToken(aux);

        return user;
    }
}
