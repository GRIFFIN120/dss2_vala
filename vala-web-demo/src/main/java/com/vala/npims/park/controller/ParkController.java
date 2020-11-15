package com.vala.npims.park.controller;

import com.vala.framework.file.controller.FileBaseController;
import com.vala.npims.park.bean.Park;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/park")
public class ParkController extends FileBaseController<Park> {
}
