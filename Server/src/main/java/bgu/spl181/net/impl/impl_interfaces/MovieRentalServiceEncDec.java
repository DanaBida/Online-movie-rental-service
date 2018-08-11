package bgu.spl181.net.impl.impl_interfaces;

import bgu.spl181.net.impl.Commands.RequestCommand;
import bgu.spl181.net.impl.Commands.RequestCommands.AddMovieCommand;
import bgu.spl181.net.impl.Commands.RequestCommands.BalanceAddCommand;
import bgu.spl181.net.impl.Commands.RequestCommands.BalanceInfoCommand;
import bgu.spl181.net.impl.Commands.RequestCommands.ChangePriceCommand;
import bgu.spl181.net.impl.Commands.RequestCommands.InfoCommand;
import bgu.spl181.net.impl.Commands.RequestCommands.RemMovieCommand;
import bgu.spl181.net.impl.Commands.RequestCommands.RentMovieCommand;
import bgu.spl181.net.impl.Commands.RequestCommands.ReturnMovieCommand;

public class MovieRentalServiceEncDec extends UserServiceTextBasedEncDec {

	@Override
	protected RequestCommand getSpecificReguest(String message) {
		//we get the string WITHOUT 'request' at the beginning
		int firstSpace = message.indexOf(" "); 
		String command = message;
		if (firstSpace!=-1) //in case of "info" there will be no space
			command = message.substring(0, firstSpace); //get string until first space. This is the name of the command
		
		if(command.equals("balance"))
			if(message.charAt(firstSpace+1)=='i') //balance will always appear with info or add after it
				command = "balance info";
			else
				command = "balance add";
		
		switch(command)
		{
		//QM = QUOTATION MARKS
			case "balance info":
				return new BalanceInfoCommand("REQUEST", command, null);
			case "balance add":
			{
				String[] messageSplit = message.split(" ");
				String[] parameters= new String [1];
				parameters[0]=messageSplit[2];//saves the amount
				return new BalanceAddCommand("REQUEST", command, parameters);
			}
			case "info":
			{
				int QM = message.indexOf('"');
				if (QM !=-1){ //movie name exist
					String[] parameters = new String[1];
					parameters[0] = message.substring(QM+1, message.lastIndexOf('"')); //movie name
					return new InfoCommand("REQUEST", command, parameters);
				}
				else { //no QMS
					return new InfoCommand("REQUEST", command, null);			
				}		
			}
			case "rent":
			{
				int QM = message.indexOf('"');
				String[] parameters= new String [1];
				parameters[0] = message.substring(QM+1, message.lastIndexOf('"')); //movie name
				return new RentMovieCommand("REQUEST", command, parameters);
			}
			case "return":
			{
				int QM = message.indexOf('"');
				String[] parameters= new String [1];
				parameters[0] = message.substring(QM+1, message.lastIndexOf('"')); //movie name
				return new ReturnMovieCommand("REQUEST", command, parameters);
			}
			case "remmovie":
			{
				int QM = message.indexOf('"');
				String[] parameters= new String [1];
				parameters[0] = message.substring(QM+1, message.lastIndexOf('"')); //movie name
				return new RemMovieCommand("REQUEST", command, parameters);
			}
			case "changeprice":
			{
				int lastQM = message.lastIndexOf('"');
				String[] parameters= new String [2];
				parameters[0] = message.substring(message.indexOf('"')+1, lastQM); //movie name
				parameters[1] = message.substring(lastQM+2); //amount
				return new ChangePriceCommand("REQUEST", command, parameters);
			}
			case "addmovie":
			{
				//syntax: addmovie <”movie name”> <amount> <price> [“banned country”,…]
				
				//get movie name - string between first and second QM
				String[] parameters= new String [4];
				int QM = message.indexOf('"'); //first QM
				String fromQM = message.substring(QM+1);
				int QM2 = fromQM.indexOf('"'); //second QM
				parameters[0] = fromQM.substring(0, QM2); //movie name
				
				//get numbers - split string between second and third QM
				QM = fromQM.indexOf('"');
				fromQM = fromQM.substring(QM+2);
				QM2 = fromQM.indexOf('"'); 
				String nums;
				if (QM2!=-1){
					nums = fromQM.substring(0, QM2-1); //both numbers
					fromQM = fromQM.substring(QM2);
				}
				else
					nums = fromQM;
				String[] messageSplit = nums.split(" ");
				parameters[1]=messageSplit[0];//saves the amount
				parameters[2]=messageSplit[1];//saves the price
				
				//gets countries list
				//syntax example: “Israel” “Iran” “Italy”
				//idea: split by QMs and only take uneven places (even ones are spaces).
				
				if (QM2!=-1){
					String[] countriesSplit = fromQM.split("\""); //split by QMs
					String countries = "";
					for (int i=1; i<countriesSplit.length; i=i+2){ //only take uneven numbers
						countries = countries + countriesSplit[i] + ",";
					}
					parameters[3]=countries.substring(0, countries.length()-1);//deletes the last comma
				}
				else
					parameters[3]="";
				
				return new AddMovieCommand("REQUEST", command, parameters);
			}
		}
			
		return null;
	}


}