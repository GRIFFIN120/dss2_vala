package com.vala.npims.park.controller;

import com.vala.framework.file.controller.FileBaseController;
import com.vala.npims.park.bean.ParkBorder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/border")
public class ParkBorderController extends FileBaseController<ParkBorder> {
}
