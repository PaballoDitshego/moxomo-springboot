package za.co.moxomo.utils;


/**
 * Created by paballo on 2016/11/16.
 */
public class Categoriser {

	public static String getCategory(String category) {

		if (category.contains("Accounts") || category.contains("Accounting, Auditing") ||
				category.contains("Accountancy (Qualified) Jobs") 
				|| category.contains("Accountancy Jobs")) {
			category = "Accounting";

		}
		if (category.contains("Beauty")) {
			category = "Beauty";

		}
		
		if (category.contains("Education") || category.contains("Education & Training")
				|| category.contains("Education Jobs") || category.contains("Training Jobs") || category.contains("Academic") ) {
			category = "Academic";

		}
		if (category.contains("Advertising")) {
			category = "Media";

		}
		if (category.contains("Banking / Finance And Investment")
				|| category.contains("Financial")
				|| category.contains("Banking, Finance, Insurance. Stockbroking")
				|| category.contains("General Insurance Jobs") 
				|| category.contains("Finance Jobs") || category.contains("Banking Jobs")) {
			category = "Banking/Finance";

		}
		if (category.contains("Civil / Building")
				|| category.contains("Building & Construction")) {
			category = "Engineering/Manufacturing";

		}
		if (category
				.contains("Freight / Shipping / Transport / Import / Export")
				|| category.contains("Transport & Aviation") || category.contains("Aviation") || category.contains("Transport & Logistics")) {
			category = "Transportation";

		}
		if (category.contains("Environmental / Horticulture / Agriculture")
				|| category.contains("Agriculture")
				|| category.contains("Botanical")
				|| category.contains("Maritime") || category.contains("Environmental, Horticulture & Agriculture")) {
			category = "Agriculture/Animal";

		}
		if (category.contains("Science & Technology") 
				|| category.contains("R&D, Science & Scientific Research")
				|| category.contains("Scientific Jobs")) {
			category = "Scientific";

		}
		if (category.contains("Part Time (no Experience Needed)")
				|| category.contains("Matriculants/Graduate/No Experience") 
				|| category.contains("Graduate Jobs")) {
			category = "Matriculants/Graduates";

		}
		if (category.contains("Government / Municipal")
				|| category.contains("Government & Local Government") || category.contains("Government & NGO")) {
			category = "Government";

		}
		if (category.contains("Textiles / Clothing Industry")
				|| category.contains("Textile/Clothing")) {
			category = "Textiles / Clothing";

		}
		
		if (category.contains("Hotel / Catering / Hospitality / Leisure")
				|| category.contains("Travel / Tourism")
				|| category.contains("Travel & Tourism")
				|| category.contains("Hospitality & Restaurant") 
				|| category.contains("Hospitality, Hotel, Catering, Tourism & Travel") 
				|| category.contains("Leisure & Tourism Jobs")
				|| category.contains("Hospitality & Catering Jobs"))
		{
			category = "Hospitality/Tourism";

		}
		if (category.contains("Management Consulting")
				|| category.contains("Business & Management") 
				|| category.contains("General Management") 
				|| category.contains("Management & Executive Jobs")) {
			category = "Management";

		}
		
		if (category
				.contains("Pharmaceutical / Medical / Healthcare / Hygiene")
				|| category.contains("Medical")
				|| category.contains("Sport & Fitness") || category.contains("Optometry") 
				|| category.contains("Health, Fitness, Medical & Optometry") 
				|| category.contains("Health & Medicine Jobs")
				|| category.contains("Pharmaceutical Jobs")) {
			category = "Health Care/Fitness";

		}
		if (category.contains("Office Support")
				|| category.contains("Admin, Office & Support Jobs") || category.contains("Admin, Office") || category.contains("Administrative Support & Secretarial") 
				|| category.contains("Admin Secretarial & PA Jobs")) {
			category = "Admin/Clerical";

		}
		if (category
				.contains("PR / Communications / Journalism / Media And Promotions")
				|| category.contains("Publishing") || category.contains("Media, Advertising, PR, Publishing & Marketing")
				|| category.contains("Media, Digital & Creative Jobs") 
				|| category.contains("Marketing & PR Jobs")) {
			category = "Media";

		}
		if (category.contains("Production")
				|| category.contains("Manufacturing, Production & Trades")) {
			category = "Engineering/Manufacturing";

		}
		if(category.contains("Agriculture and Mining Jobs")){
			category = "Mining";
		}
		
		if (category.contains("Arts")
				|| category.contains("Entertainment")) {
			category = "Arts/Entertainment";

		}
		if (category.contains("FMCG, Retail & Wholesale") || category.contains("FMCG, Retail, Wholesale & Supply Chain") 
				|| category.contains("Retail Jobs") || category.contains("FMCG Jobs")) {
			category = "Retail";

		}
		if (category.contains("Information Technology Industry") || category.contains("IT and Telecommunications") 
				|| category.contains("IT Contractor Jobs") || category.contains("IT & Telecoms Jobs")) {
			category = "Information Technology";

		}
		if(category.contains("Engineering, Technical, Production & Manufacturing") 
				|| category.contains("Manufacturing Jobs") 
				|| category.contains("Engineering Jobs") || category.contains("Construction & Property Jobs")){
			category = "Engineering/Manufacturing";
		}
		
		
		if (category.contains("Motor") 
				|| category.contains("Motoring & Automotive Jobs ")) {
			category = "Motor Industry";

		}
		if (category.contains("Property")) {
			category = "Property/Real Eastate";

		}
		if (category.contains("Safety And Security")
				|| category.contains("Safety, Security & Defence") || category.contains("Safety & Security") 
				|| category.contains("Security Jobs")) {
			category = "Safety/Security";

		}
		if (category.contains("Human Resources")
				|| category.contains("Human Resources & Recruitment") 
				|| category.contains("Recruitment Consultancy Jobs") 
				|| category.contains("Human Resources Jobs")) {
			category = "Human Resource";

		}
		if (category.contains("Sales And Purchasing")
				|| category.contains("Sales") || category.contains("Marketing") || category.contains("Sales & Purchasing") 
				|| category.contains("Sales Jobs") || category.contains("Purchasing Jobs")) {
			category = "Sales/Purchasing";

		}
		if (category.contains("Distribution, Warehousing & Freight") || category.contains("Transport & Logistics Jobs")) {
			category = "Transportation";

		}
		if (category.contains("Chemical , Petrochemical, Oil & Gas") || category.contains("Petrochemical") || category.contains("Oil & Gas")) {
			
			category = "Chemical/Petroleum";

		}
		if (category.contains("Social & Community")) {
			category = "NGO / Non-profit";

		}
		if(category.contains("Design, Architecture & Property")){
			category= "Design";
		}
		if(category.contains("Legal Jobs")){
			category = "Legal";
		}
		if(category.contains("Customer Service Jobs")){
			category ="Customer Service";
		}
		
		

		return category;
	}
}
