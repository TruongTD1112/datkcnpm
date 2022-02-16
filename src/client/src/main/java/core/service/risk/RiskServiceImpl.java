package core.service.risk;

import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import core.model.risk.Risk;
import core.utils.Utils;

public class RiskServiceImpl implements RiskServiceInterface{

	@Override
	public List<Risk> readRiskRelationInfo(Reader fileReader) {
		List<Risk> allRisks = new ArrayList<Risk>();
		Map<String,List<String>> parentRiskMap = new HashMap<String,List<String>>();
		try {
			CSVReader csvReader = new CSVReaderBuilder(fileReader)
                    .withSkipLines(1) 
                    .build(); 
			String [] nextRecord;
			while ((nextRecord = csvReader.readNext()) != null) {
				if (nextRecord.length <= 1) continue;
	            parentRiskMap.put(nextRecord[0], Utils.convertStringToList(nextRecord[1]));
	        } 
			for(Entry<String, List<String>> entry: parentRiskMap.entrySet()) {
				allRisks.add(new Risk(entry.getKey()));
			}
			Collections.sort(allRisks,(r1,r2)->{
				return Integer.parseInt(r1.getId())-Integer.parseInt(r2.getId());
			});
			for(Risk risk:allRisks) {
				List<String>strParent = parentRiskMap.get(risk.getId());
				if(Integer.parseInt(strParent.get(0))!=0) {
					risk.setParentRisk(convertToParentRiskList(strParent, allRisks));
				}
			}
			
			return allRisks;
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		return null;
	}

	@Override
	public List<Risk> convertToParentRiskList(List<String> strParentList, List<Risk> risks) {
		return risks.stream().filter(risk -> strParentList.contains(risk.getId())).collect(Collectors.toList());
	}

	@Override
	public void readRiskDistribution(Reader fileReader, List<Risk> risks) {
		try {
			CSVReader csvReader = new CSVReaderBuilder(fileReader)
                    .build(); 
			String [] nextRecord;
			Map<String,List<Double>> probMap = new HashMap<String,List<Double>>();
			while ((nextRecord = csvReader.readNext()) != null) { 
				ArrayList<Double> childList = new ArrayList<Double>();
	            for(int i=1;i<nextRecord.length;i++) {
	            	childList.add(Double.parseDouble(nextRecord[i]));
	            	
	            }
	            probMap.put(nextRecord[0], childList);
	            } 
			for(Risk risk:risks) {
				if(risk.getParentRisk()==null) {
					risk.setProbability(0.5);
				}
				risk.setProbabilityList(probMap.get(risk.getId()));
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}

	@Override
	public Risk getRiskById(String id,List<Risk> risks) {
		for(Risk r:risks) {
			if(r.getId().equals(id)) {
				
				return r;
			}
		}
		return null;
	}

}
