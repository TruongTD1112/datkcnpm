package core.utils;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.math3.special.Erf;

import core.model.risk.Risk;
public class Utils {
	List<String> getListFromString(String str) {
			return Arrays.asList(str.split(","));
	}
	public static double gauss(double givenTime, double expectedTime,double standardDeviation) {
//		return Gaussian.cdf(givenTime, expectedTime, standardDeviation);
		double z = (givenTime - expectedTime)/standardDeviation;
		double result = (1+Erf.erf(z/Math.sqrt(2)))/2;
		return result;
	}
	
	public static List<String> convertStringToList(String str) {
		return (str.split(" ").length!=0)?Arrays.asList(str.split(" ")):null;
	}

	public static int [][] matrix(int row,int column,int value){
		int [][] mat = new int[row][column];
		for(int i=0;i<row;i++)
		{
			for(int j=0;j<column;j++)
			{
				mat[i][j] = value;
			}
		}
		return mat;
	}
	public static int[] getProb(int [][] mat) {
		int [] prob = new int [mat[0].length];
		Arrays.fill(prob, 1);
		for(int i=0;i<mat[0].length;i++) {
			int probArray =1;
			for(int j=0;j<mat[1].length;j++) {
				probArray *= mat[i][j];
			}
			prob[i] = probArray;
		}
		return prob;
	}
	public static List<Risk> riskNeedUpdateBefore(List<Risk> risks,int [] prob){
		
		return risks.stream().filter(r-> prob[Integer.parseInt(r.getId())-1]==1).collect(Collectors.toList());
	}
	public static void updateMatrix(List<Risk> nodeUpdates,int [][] matrix,int[] prob) {
		nodeUpdates.stream().filter(r->prob[Integer.parseInt(r.getId())-1]==1).forEach(entry->{
			setValueCol(matrix,Integer.parseInt(entry.getId())-1, 1);
			setValueRow(matrix,Integer.parseInt(entry.getId())-1, 0);
		});
	}
	public static void setValueRow(int [][] mat,int row,int value) {
		for(int i=0;i<mat[0].length;i++) {
			if(i==row) {
				for(int j=0;j<mat[1].length;j++) {
					mat[i][j] = value;
				}
			}
			
		}
	}
	public static String round(double num) {
		DecimalFormat df = new DecimalFormat("#.##");
		double newNum = (double) Math.round(num*10000)/10000;
		df.setRoundingMode(RoundingMode.CEILING);
		return df.format(newNum);	
	}
	public static void setValueCol(int [][] mat,int col,int value) {
		for(int i=0;i<mat[1].length;i++) {
			if(i==col) {
				for(int j=0;j<mat[0].length;j++) {
					mat[j][i] = value;
				}
			}
			
		}
	}
	

}
