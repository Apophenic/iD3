package id3.utils;

import java.util.ArrayList;

public final class Distancing
{
	protected Distancing() {}
	
	/** Uses a string distancing technique to check similarity of two strings
	 * and returns their their similarity factor, between 0 and 1.
	 */
	public static float compare(String string1, String string2)
	{
		float score1;
		float score2;
		score1 = stringSimilarity(string1, string2);
		score2 = stringSimilarity(string2, string1);
		
		if(score1 > score2)
		{
			return score1;
		}
		else
		{
			return score2;
		}
	}
	
	private static float stringSimilarity(String string1, String string2)
	{
	    if(string1==null || string2==null)
	    {
	        return (float) 0.5;
	    }

	    float score = 0;

	    ArrayList<String> charactersString1 = new ArrayList<String>();
	    ArrayList<String> charactersString2 = new ArrayList<String>();

	    for(int i=0 ; i < string1.length() ; i++)
	    {
	        String aCharacter = String.valueOf(string1.charAt(i));
	        charactersString1.add(aCharacter); 
	    }

	    for(int i=0 ; i < string2.length() ; i++)
	    {
	        String aCharacter = String.valueOf(string2.charAt(i));
	        charactersString2.add(aCharacter); 
	    }

	    boolean differentSize = false;
	    ArrayList<String> arrayLargo = new ArrayList<String>();
	    ArrayList<String> arrayCorto = new ArrayList<String>();
	    if(charactersString1.size() < charactersString2.size())
	    {
	        arrayLargo = charactersString2;
	        arrayCorto = charactersString1;
	        differentSize = true;
	    } 
	    else
	    {
	        if (charactersString2.size() < charactersString1.size())
	        {
	            arrayLargo = charactersString1;
	            arrayCorto = charactersString2;
	            differentSize = true;

	        } 
	        else 
	        {
	            if (charactersString2.size() == charactersString1.size())
	            {
	                for (int i = 0 ; i < charactersString1.size() ; i++)
	                {
	                    String elementoS1 = charactersString1.get(i);
	                    String elementoS2 = charactersString2.get(i);

	                    if (elementoS1.equalsIgnoreCase(elementoS2))
	                    {
	                        score = score + 1;
	                    }
	                    else 
	                    {
	                        if (0 < i)
	                        {
	                            String elementoS1Past = charactersString1.get(i - 1);
	                            String elementoS2Past = charactersString2.get(i - 1);

	                            if (elementoS1Past.equalsIgnoreCase(elementoS2) && elementoS1.equalsIgnoreCase(elementoS2Past))
	                            {
	                                score++;
	                            }
	                        }
	                    }
	                }
	            }
	            else
	            {
	            	//Do nothing
	            }

	            score = score/charactersString1.size();
	        }
	    }

	    if (differentSize)
	    {
	        int indice=0;
	        for (int i=0; i < arrayCorto.size();i++)
	        {
	            String elementoS1 = arrayLargo.get(i);
	            String elementoS2 = arrayCorto.get(i);

	            if (elementoS1.equalsIgnoreCase(elementoS2))
	            {
	                score++;
	            }
	            else 
	            {
	                boolean switched = false;
	                if (i + 1 < arrayCorto.size())
	                {
	                    String elementoS1Future = arrayLargo.get(i + 1);
	                    String elementoS2Future = arrayCorto.get(i + 1);
	                    if (elementoS1Future.equalsIgnoreCase(elementoS2)
	                            && elementoS1.equalsIgnoreCase(elementoS2Future))
	                    {
	                        score++;
	                        i++;
	                        switched = true;
	                    }
	                }
	                if (!switched)
	                {
	                    arrayLargo.remove(i);                       
	                    indice = i;
	                    i = i - 1;
	                }
	            }
	            if (arrayLargo.size() == arrayCorto.size())
	            {
	                break;
	            }
	        }
	        if (arrayLargo.size() == arrayCorto.size()) 
	        {
	            for (int i = indice; i < charactersString1.size() && i < charactersString2.size(); i++)
	            {
	                String elementoS1 = arrayLargo.get(i);
	                String elementoS2 = arrayCorto.get(i);
	                if (elementoS1.equalsIgnoreCase(elementoS2))
	                {
	                    score++;
	                }
	                else
	                {
	                    if (0 < i)
	                    {
	                        String elementoS1Past = charactersString1.get(i - 1);
	                        String elementoS2Past = charactersString2.get(i - 1);
	                        if (elementoS1Past.equalsIgnoreCase(elementoS2)
	                                && elementoS1.equalsIgnoreCase(elementoS2Past))
	                        {
	                            score++;
	                        }
	                    }
	                }
	            }
	        }
	        int normalize;
	        if (charactersString2.size() <= charactersString1.size())
	        {
	            normalize = charactersString1.size();
	        }else
	        {
	            normalize = charactersString2.size();
	        }
	        score = score/normalize;
	    }
	    return score;
	}
}
