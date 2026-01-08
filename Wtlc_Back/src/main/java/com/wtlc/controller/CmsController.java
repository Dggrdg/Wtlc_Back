package com.wtlc.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wtlc.service.CmsService;

@RestController
@RequestMapping("/cms")
public class CmsController {

	private final CmsService cmsService;

	CmsController(CmsService cmsService) {
		this.cmsService = cmsService;
	}

	@PostMapping("/searchIpLocation")
	public void SearchIpLocation() throws Exception {

		cmsService.batchLookupAndWrite();

	}
}
