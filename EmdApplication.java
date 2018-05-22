import org.apache.commons.cli.*;

public class EmdApplication {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Options options = new Options();

		Option coverImage = new Option("c", "coverImage", true, "Cover or Stego image file path.");
		coverImage.setRequired(true);
		options.addOption(coverImage);
		
		Option secretImage = new Option("s", "secretImage", true, "Secret image file path.");
		secretImage.setRequired(false);
		options.addOption(secretImage);
		
		Option method = new Option("m", "method", true, "Enter method like embed or extract.");
		method.setRequired(true);
		options.addOption(method);
		
		Option outputImage = new Option("o", "outputImage", true, "Output image file path.");
		outputImage.setRequired(true);
		options.addOption(outputImage);
		
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		CommandLine cmd;
		try {
			cmd=parser.parse(options, args);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			formatter.printHelp("utility-name", options);
			System.exit(1);
			return;
		}
		
		String img1		= cmd.getOptionValue("c");
		String mtd		= cmd.getOptionValue("m");
		String output	= cmd.getOptionValue("o");
		
		if( mtd.equals("embed") )	{ 

			String img2		= cmd.getOptionValue("s");
			if(img2 == null){
				System.out.println("If you want to do embeding, you have to enter secret image.");
				System.exit(1);
			}
			Emd emd = new Emd(img1, img2, output);
			emd.encodeImage(); 
		} 
		else if( mtd.equals("extract")) { 

			Emd emd = new Emd(img1, null, output);
			emd.decodeImage(); 
			}
		else { System.out.println("Wrong method. Only embed or extract."); }


	}

}