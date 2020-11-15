package com.vala.npims.park.controller;

import com.vala.framework.file.controller.FileBaseController;
import com.vala.npims.park.bean.ParkFacility;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/facility")
public class ParkFacilityController extends FileBaseController<ParkFacility> {
}
