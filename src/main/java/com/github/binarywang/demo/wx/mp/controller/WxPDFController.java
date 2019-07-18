package com.github.binarywang.demo.wx.mp.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

@RestController
@RequestMapping("/wx/mp/{appid}")
public class WxPDFController {
	
	private static final String filePath = "/Users/zhuqimin/Documents/generatedPDF/";
	
	private static final String templatePath = "/Users/zhuqimin/Documents/generatedPDF/applicationFormTemplate.pdf";
	
	@RequestMapping("/generatePdf")
	protected String handleGeneratePdf(@PathVariable String appid, @RequestParam String idNO) {
		try {
			File file = ResourceUtils.getFile(templatePath);
			if(file.exists()) {
				InputStream input = new FileInputStream(file);
				PdfReader reader = new PdfReader(input);
				String targetPath = filePath+"test.pdf";
				PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(targetPath));
				AcroFields form = stamper.getAcroFields();
				fillData(form, this.data());
				stamper.setFormFlattening(true);
				Image image = Image.getInstance("/Users/zhuqimin/Documents/logo.png");
				image.scaleToFit(100, 125);
				PdfContentByte content = null;
				int pageCount = reader.getNumberOfPages();
				content = stamper.getOverContent(pageCount);
				image.setAbsolutePosition(60, 333);
				content.addImage(image);
				stamper.close();
				reader.close();
				return "generate success";
			}else {
				return "template file not found";
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "exception";
	}
	
	private void fillData(AcroFields fields, Map<String, String> data) throws IOException, DocumentException {
		for(String key: data.keySet()) {
			String value = data.get(key);
			fields.setField(key, value);
		}
	}
	
	private Map<String, String> data(){
		Map<String, String> data = new HashMap<String, String>();
		data.put("SICARD_xm", "名字");
		data.put("SICARD_xb", "gender");
		data.put("mz", "nation");
		data.put("SICARD_gmsfhm_1", "1");
		data.put("SICARD_gmsfhm_2", "2");
		data.put("SICARD_gmsfhm_3", "3");
		data.put("SICARD_gmsfhm_4", "4");
		data.put("SICARD_gmsfhm_5", "5");
		data.put("SICARD_gmsfhm_6", "6");
		data.put("SICARD_gmsfhm_7", "7");
		data.put("SICARD_gmsfhm_8", "8");
		data.put("SICARD_gmsfhm_9", "9");
		data.put("SICARD_gmsfhm_10", "9");
		data.put("SICARD_gmsfhm_11", "8");
		data.put("SICARD_gmsfhm_12", "7");
		data.put("SICARD_date", new Date().toLocaleString());
		return data;
	}
}
