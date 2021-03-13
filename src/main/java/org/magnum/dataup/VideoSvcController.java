/*
 * 
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.magnum.dataup;

import org.apache.commons.compress.utils.IOUtils;
import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.magnum.dataup.utils.VideoUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

@Controller
public class VideoSvcController {

	/**
	 * You will need to create one or more Spring controllers to fulfill the
	 * requirements of the assignment. If you use this file, please rename it
	 * to something other than "AnEmptyController"
	 * 
	 * 
		 ________  ________  ________  ________          ___       ___  ___  ________  ___  __       
		|\   ____\|\   __  \|\   __  \|\   ___ \        |\  \     |\  \|\  \|\   ____\|\  \|\  \     
		\ \  \___|\ \  \|\  \ \  \|\  \ \  \_|\ \       \ \  \    \ \  \\\  \ \  \___|\ \  \/  /|_   
		 \ \  \  __\ \  \\\  \ \  \\\  \ \  \ \\ \       \ \  \    \ \  \\\  \ \  \    \ \   ___  \  
		  \ \  \|\  \ \  \\\  \ \  \\\  \ \  \_\\ \       \ \  \____\ \  \\\  \ \  \____\ \  \\ \  \ 
		   \ \_______\ \_______\ \_______\ \_______\       \ \_______\ \_______\ \_______\ \__\\ \__\
		    \|_______|\|_______|\|_______|\|_______|        \|_______|\|_______|\|_______|\|__| \|__|
                                                                                                                                                                                                                                                                        
	 * 
	 */

	@GetMapping("/video")
	@ResponseBody Collection<Video> getVideoList() {
		return VideoUtils.getVideos();
	}

	@PostMapping("/video")
	@ResponseBody Video addVideo(@RequestBody Video video) {
		return VideoUtils.save(video);
	}

	@GetMapping("/video/{id}/data")
	@ResponseBody byte[] getData(@PathVariable("id") Long id) throws IOException {

		Video video = VideoUtils.getById(id);
		VideoFileManager videoFileManager = VideoFileManager.get();

		try {
			videoFileManager.hasVideoData(video);
			File initialFile = new File("videos/video" + id +".mpg");
			InputStream targetStream = new FileInputStream(initialFile);
			return IOUtils.toByteArray(targetStream);
		} catch (Exception e) {
			throw new ResponseStatusException(
					HttpStatus.NOT_FOUND, "Video Not Found", e);
		}
	}


	@PostMapping("/video/{id}/data")
	@ResponseBody
	VideoStatus setVideoData(@PathVariable("id") Long id,
							   @RequestParam("data") MultipartFile videoData) throws IOException {
		Video video = VideoUtils.getById(id);
		VideoFileManager videoFileManager = VideoFileManager.get();
		InputStream in = videoData.getInputStream();

		try {
			VideoStatus videoStatus = new VideoStatus(VideoStatus.VideoState.READY);
			videoFileManager.saveVideoData(video, in);
			return videoStatus;
		} catch (Exception e) {
			throw new ResponseStatusException(
					HttpStatus.NOT_FOUND, "Video Not Found", e);
		}
	}
}
