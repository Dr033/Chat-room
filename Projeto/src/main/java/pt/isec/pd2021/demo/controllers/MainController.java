package pt.isec.pd2021.demo.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.isec.pd2021.demo.Server.RMI.ServerRMIInterface;

@RestController
@RequestMapping("main")
public class MainController {

    ServerRMIInterface server;

    public MainController(ServerRMIInterface server)
    {
        this.server = server;
    }

}
