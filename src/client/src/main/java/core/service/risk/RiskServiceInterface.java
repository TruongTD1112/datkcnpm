package core.service.risk;

import java.io.Reader;
import java.util.List;

import core.model.risk.Risk;

public interface RiskServiceInterface {
	public List<Risk> readRiskRelationInfo(Reader reader);
	public List<Risk> convertToParentRiskList(List<String> strParentList,List<Risk> risks);
	public void readRiskDistribution(Reader reader, List<Risk> risks);
	public Risk getRiskById(String id,List<Risk> risks);
}
