package net.jamezo97.clonecraft.clone;

import java.util.Random;

import net.minecraft.util.ResourceLocation;

public class NameRegistry {
	

//	public static final ResourceLocation guiImage = new ResourceLocation("CloneCraft:textures/skins/male/male1.png");
//	public static final ResourceLocation guiImage = new ResourceLocation("CloneCraft:textures/skins/female/female1.png");
	
	
	
	public static final ResourceLocation defaultResource = new ResourceLocation("textures/entity/steve.png");
	
	static Random rand = new Random();
	
	
	private static ResourceLocation[] femaleSkins;
	private static ResourceLocation[] maleSkins;
	
	public static ResourceLocation getDefaultSkinForClone(EntityClone clone)
	{
		boolean gender = clone.getOptions().female.get();
		
		String name = clone.getName();
		
		if(name != null)
		{
			int hash = name.hashCode();
			
			int sum = 0;
			
			for(int a = 0; a < 32; a++)
			{
				sum += (hash >> a)&1;
			}
			sum /= 2;
			
			if(gender)
			{
				return femaleSkins[sum];
			}
			else
			{
				return maleSkins[sum];
			}
			
			
		}
		
		return defaultResource;
	}
	
	
	private static void init()
	{
		int q = 16;
		
		for(int a = 0; a < 2; a++)
		{
			ResourceLocation[] list;
			String prefix = "CloneCraft:textures/skins/";
			
			if(a == 0)
			{
				femaleSkins = new ResourceLocation[q];
				list = femaleSkins;
				prefix += "female/female";
			}
			else
			{
				maleSkins = new ResourceLocation[q];
				list = maleSkins;
				prefix += "male/male";
			}

			for(int b = 1; b <= q; b++)
			{
				list[b-1] = new ResourceLocation(prefix + (b) + ".png");
			}
		}
	}
	
	public static String getGirlName()
	{
		return girlNames[rand.nextInt(girlNames.length)];
	}
	
	public static String getBoyName()
	{
		return boyNames[rand.nextInt(boyNames.length)];
	}
	
	static String[] boyNames = new String[]{
			"Noah",
			"Liam",
			"Jacob",
			"Mason",
			"William",
			"Ethan",
			"Michael",
			"Alexander",
			"Jayden",
			"Daniel",
			"Elijah",
			"Aiden",
			"James",
			"Benjamin",
			"Matthew",
			"Jackson",
			"Logan",
			"David",
			"Anthony",
			"Joseph",
			"Joshua",
			"Andrew",
			"Lucas",
			"Gabriel",
			"Samuel",
			"Christopher",
			"John",
			"Dylan",
			"Isaac",
			"Ryan",
			"Nathan",
			"Carter",
			"Caleb",
			"Luke",
			"Christian",
			"Hunter",
			"Henry",
			"Owen",
			"Landon",
			"Jack",
			"Wyatt",
			"Jonathan",
			"Eli",
			"Isaiah",
			"Sebastian",
			"Jaxon",
			"Julian",
			"Brayden",
			"Gavin",
			"Levi",
			"Aaron",
			"Oliver",
			"Jordan",
			"Nicholas",
			"Evan",
			"Connor",
			"Charles",
			"Jeremiah",
			"Cameron",
			"Adrian",
			"Thomas",
			"Robert",
			"Tyler",
			"Colton",
			"Austin",
			"Jace",
			"Angel",
			"Dominic",
			"Josiah",
			"Brandon",
			"Ayden",
			"Kevin",
			"Zachary",
			"Parker",
			"Blake",
			"Jose",
			"Chase",
			"Grayson",
			"Jason",
			"Ian",
			"Bentley",
			"Adam",
			"Xavier",
			"Cooper",
			"Justin",
			"Nolan",
			"Hudson",
			"Easton",
			"Jase",
			"Carson",
			"Nathaniel",
			"Jaxson",
			"Kayden",
			"Brody",
			"Lincoln",
			"Luis",
			"Tristan",
			"Damian",
			"Camden",
			"Juan",
			"Vincent",
			"Bryson",
			"Ryder",
			"Asher",
			"Carlos",
			"Jesus",
			"Micah",
			"Maxwell",
			"Mateo",
			"Alex",
			"Max",
			"Leo",
			"Elias",
			"Cole",
			"Miles",
			"Silas",
			"Bryce",
			"Eric",
			"Brantley",
			"Sawyer",
			"Declan",
			"Braxton",
			"Kaiden",
			"Colin",
			"Timothy",
			"Santiago",
			"Antonio",
			"Giovanni",
			"Hayden",
			"Diego",
			"Leonardo",
			"Bryan",
			"Miguel",
			"Roman",
			"Jonah",
			"Steven",
			"Ivan",
			"Kaleb",
			"Wesley",
			"Richard",
			"Jaden",
			"Victor",
			"Ezra",
			"Joel",
			"Edward",
			"Jayce",
			"Aidan",
			"Preston",
			"Greyson",
			"Brian",
			"Kaden",
			"Ashton",
			"Alan",
			"Patrick",
			"Kyle",
			"Riley",
			"George",
			"Jesse",
			"Jeremy",
			"Marcus",
			"Harrison",
			"Jude",
			"Weston",
			"Ryker",
			"Alejandro",
			"Jake",
			"Axel",
			"Grant",
			"Maddox",
			"Theodore",
			"Emmanuel",
			"Cayden",
			"Emmett",
			"Brady",
			"Bradley",
			"Gael",
			"Malachi",
			"Oscar",
			"Abel",
			"Tucker",
			"Jameson",
			"Caden",
			"Abraham",
			"Mark",
			"Sean",
			"Ezekiel",
			"Kenneth",
			"Gage",
			"Everett",
			"Kingston",
			"Nicolas",
			"Zayden",
			"King",
			"Bennett",
			"Calvin",
			"Avery",
			"Tanner",
			"Paul",
			"Kai",
			"Maximus",
			"Rylan",
			"Luca",
			"Graham",
			"Omar",
			"Derek",
			"Jayceon",
			"Jorge",
			"Peter",
			"Peyton",
			"Devin",
			"Collin",
			"Andres",
			"Jaiden",
			"Cody",
			"Zane",
			"Amir",
			"Corbin",
			"Francisco",
			"Xander",
			"Eduardo",
			"Conner",
			"Javier",
			"Jax",
			"Myles",
			"Griffin",
			"Iker",
			"Garrett",
			"Damien",
			"Simon",
			"Zander",
			"Seth",
			"Travis",
			"Charlie",
			"Cristian",
			"Trevor",
			"Zion",
			"Lorenzo",
			"Dean",
			"Gunner",
			"Chance",
			"Elliot",
			"Lukas",
			"Cash",
			"Elliott",
			"Israel",
			"Manuel",
			"Josue",
			"Jasper",
			"Keegan",
			"Finn",
			"Spencer",
			"Stephen",
			"Fernando",
			"Ricardo",
			"Mario",
			"Jeffrey",
			"Shane",
			"Clayton",
			"Reid",
			"Erick",
			"Cesar",
			"Paxton",
			"Martin",
			"Raymond",
			"Judah",
			"Trenton",
			"Johnny",
			"Andre",
			"Tyson",
			"Beau",
			"Landen",
			"Caiden",
			"Maverick",
			"Dominick",
			"Troy",
			"Kyler",
			"Hector",
			"Cruz",
			"Beckett",
			"Johnathan",
			"Donovan",
			"Edwin",
			"Kameron",
			"Marco",
			"Drake",
			"Edgar",
			"Holden",
			"Rafael",
			"Dante",
			"Jaylen",
			"Emiliano",
			"Waylon",
			"Andy",
			"Alexis",
			"Rowan",
			"Felix",
			"Drew",
			"Emilio",
			"Gregory",
			"Karter",
			"Brooks",
			"Dallas",
			"Lane",
			"Anderson",
			"Jared",
			"Skyler",
			"Angelo",
			"Shawn",
			"Aden",
			"Erik",
			"Dalton",
			"Fabian",
			"Sergio",
			"Milo",
			"Louis",
			"Titus",
			"Kendrick",
			"Braylon",
			"August",
			"Dawson",
			"Reed",
			"Emanuel",
			"Arthur",
			"Jett",
			"Leon",
			"Brendan",
			"Frank",
			"Marshall",
			"Emerson",
			"Desmond",
			"Derrick",
			"Colt",
			"Karson",
			"Messiah",
			"Zaiden",
			"Braden",
			"Amari",
			"Roberto",
			"Romeo",
			"Joaquin",
			"Malik",
			"Walter",
			"Brennan",
			"Pedro",
			"Knox",
			"Nehemiah",
			"Julius",
			"Grady",
			"Allen",
			"Ali",
			"Archer",
			"Kamden",
			"Dakota",
			"Maximiliano",
			"Ruben",
			"Quinn",
			"Barrett",
			"Tate",
			"Corey",
			"Adan",
			"Braylen",
			"Marcos",
			"Remington",
			"Phillip",
			"Kason",
			"Major",
			"Kellan",
			"Cohen",
			"Walker",
			"Gideon",
			"Taylor",
			"River",
			"Jayson",
			"Brycen",
			"Abram",
			"Cade",
			"Matteo",
			"Dillon",
			"Damon",
			"Dexter",
			"Kolton",
			"Phoenix",
			"Noel",
			"Brock",
			"Porter",
			"Philip",
			"Enrique",
			"Leland",
			"Ty",
			"Esteban",
			"Danny",
			"Jay",
			"Gerardo",
			"Keith",
			"Kellen",
			"Gunnar",
			"Armando",
			"Zachariah",
			"Orion",
			"Ismael",
			"Colby",
			"Pablo",
			"Ronald",
			"Atticus",
			"Trey",
			"Quentin",
			"Ryland",
			"Kash",
			"Raul",
			"Enzo",
			"Julio",
			"Darius",
			"Rodrigo",
			"Landyn",
			"Donald",
			"Bruce",
			"Jakob",
			"Kade",
			"Ari",
			"Keaton",
			"Albert",
			"Muhammad",
			"Rocco",
			"Solomon",
			"Rhett",
			"Cason",
			"Jaime",
			"Scott",
			"Chandler",
			"Mathew",
			"Maximilian",
			"Russell",
			"Dustin",
			"Ronan",
			"Tony",
			"Cyrus",
			"Jensen",
			"Hugo",
			"Saul",
			"Trent",
			"Deacon",
			"Davis",
			"Colten",
			"Malcolm",
			"Mohamed",
			"Devon",
			"Izaiah",
			"Randy",
			"Ibrahim",
			"Jerry",
			"Prince",
			"Tristen",
			"Alec",
			"Chris",
			"Dennis",
			"Clark",
			"Gustavo",
			"Mitchell",
			"Rory",
			"Jamison",
			"Leonel",
			"Finnegan",
			"Pierce",
			"Nash",
			"Kasen",
			"Khalil",
			"Darren",
			"Moses",
			"Issac",
			"Adriel",
			"Lawrence",
			"Braydon",
			"Jaxton",
			"Alberto",
			"Justice",
			"Curtis",
			"Larry",
			"Warren",
			"Zayne",
			"Yahir",
			"Jimmy",
			"Uriel",
			"Finley",
			"Nico",
			"Thiago",
			"Armani",
			"Jacoby",
			"Jonas",
			"Rhys",
			"Casey",
			"Tobias",
			"Frederick",
			"Jaxen",
			"Kobe",
			"Franklin",
			"Ricky",
			"Talon",
			"Ace",
			"Marvin",
			"Alonzo",
			"Arjun",
			"Jalen",
			"Alfredo",
			"Moises",
			"Sullivan",
			"Francis",
			"Case",
			"Brayan",
			"Alijah",
			"Arturo",
			"Lawson",
			"Raylan",
			"Mekhi",
			"Nikolas",
			"Carmelo",
			"Byron",
			"Nasir",
			"Reece",
			"Royce",
			"Sylas",
			"Ahmed",
			"Mauricio",
			"Beckham",
			"Roy",
			"Payton",
			"Raiden",
			"Korbin",
			"Maurice",
			"Ellis",
			"Aarav",
			"Johan",
			"Gianni",
			"Kayson",
			"Aldo",
			"Arian",
			"Isaias",
			"Jamari",
			"Kristopher",
			"Uriah",
			"Douglas",
			"Kane",
			"Milan",
			"Skylar",
			"Dorian",
			"Tatum",
			"Wade",
			"Cannon",
			"Quinton",
			"Bryant",
			"Toby",
			"Dane",
			"Sam",
			"Moshe",
			"Asa",
			"Mohammed",
			"Joe",
			"Kieran",
			"Roger",
			"Channing",
			"Daxton",
			"Ezequiel",
			"Orlando",
			"Matias",
			"Malakai",
			"Nathanael",
			"Zackary",
			"Boston",
			"Ahmad",
			"Dominik",
			"Lance",
			"Alvin",
			"Conor",
			"Odin",
			"Cullen",
			"Mohammad",
			"Deandre",
			"Benson",
			"Gary",
			"Blaine",
			"Carl",
			"Sterling",
			"Nelson",
			"Kian",
			"Salvador",
			"Luka",
			"Nikolai",
			"Nixon",
			"Niko",
			"Bowen",
			"Kyrie",
			"Brenden",
			"Callen",
			"Vihaan",
			"Luciano",
			"Terry",
			"Demetrius",
			"Raphael",
			"Ramon",
			"Xzavier",
			"Amare",
			"Rohan",
			"Reese",
			"Quincy",
			"Eddie",
			"Noe",
			"Yusuf",
			"London",
			"Hayes",
			"Jefferson",
			"Matthias",
			"Kelvin",
			"Terrance",
			"Madden",
			"Bentlee",
			"Layne",
			"Harvey",
			"Sincere",
			"Kristian",
			"Julien",
			"Melvin",
			"Harley",
			"Emmitt",
			"Neil",
			"Rodney",
			"Winston",
			"Hank",
			"Ayaan",
			"Ernesto",
			"Jeffery",
			"Alessandro",
			"Lucian",
			"Rex",
			"Wilson",
			"Mathias",
			"Memphis",
			"Princeton",
			"Santino",
			"Jon",
			"Tripp",
			"Lewis",
			"Trace",
			"Dax",
			"Eden",
			"Joey",
			"Nickolas",
			"Neymar",
			"Bruno",
			"Marc",
			"Crosby",
			"Cory",
			"Kendall",
			"Abdullah",
			"Allan",
			"Davion",
			"Hamza",
			"Soren",
			"Brentley",
			"Jasiah",
			"Edison",
			"Harper",
			"Tommy",
			"Morgan",
			"Zain",
			"Flynn",
			"Roland",
			"Theo",
			"Chad",
			"Lee",
			"Bobby",
			"Rayan",
			"Samson",
			"Brett",
			"Kylan",
			"Branson",
			"Bronson",
			"Ray",
			"Arlo",
			"Lennox",
			"Stanley",
			"Zechariah",
			"Kareem",
			"Micheal",
			"Reginald",
			"Alonso",
			"Casen",
			"Guillermo",
			"Leonard",
			"Augustus",
			"Tomas",
			"Billy",
			"Conrad",
			"Aryan",
			"Makai",
			"Elisha",
			"Westin",
			"Otto",
			"Adonis",
			"Jagger",
			"Keagan",
			"Dayton",
			"Leonidas",
			"Kyson",
			"Brodie",
			"Alden",
			"Aydin",
			"Valentino",
			"Harry",
			"Willie",
			"Yosef",
			"Braeden",
			"Marlon",
			"Terrence",
			"Lamar",
			"Shaun",
			"Aron",
			"Blaze",
			"Layton",
			"Duke",
			"Legend",
			"Jessie",
			"Terrell",
			"Clay",
			"Dwayne",
			"Felipe",
			"Kamari",
			"Gerald",
			"Kody",
			"Kole",
			"Maxim",
			"Omari",
			"Chaim",
			"Crew",
			"Lionel",
			"Vicente",
			"Bo",
			"Sage",
			"Rogelio",
			"Jermaine",
			"Gauge",
			"Will",
			"Emery",
			"Giovani",
			"Ronnie",
			"Elian",
			"Hendrix",
			"Javon",
			"Rayden",
			"Alexzander",
			"Ben",
			"Camron",
			"Jamarion",
			"Kolby",
			"Remy",
			"Jamal",
			"Urijah",
			"Jaydon",
			"Kyree",
			"Ariel",
			"Braiden",
			"Cassius",
			"Triston",
			"Jerome",
			"Junior",
			"Landry",
			"Wayne",
			"Killian",
			"Jamie",
			"Davian",
			"Lennon",
			"Samir",
			"Oakley",
			"Rene",
			"Ronin",
			"Tristian",
			"Darian",
			"Giancarlo",
			"Jadiel",
			"Amos",
			"Eugene",
			"Mayson",
			"Vincenzo",
			"Alfonso",
			"Brent",
			"Cain",
			"Callan",
			"Leandro",
			"Callum",
			"Darrell",
			"Atlas",
			"Fletcher",
			"Jairo",
			"Jonathon",
			"Kenny",
			"Tyrone",
			"Adrien",
			"Markus",
			"Thaddeus",
			"Zavier",
			"Marcel",
			"Marquis",
			"Misael",
			"Abdiel",
			"Draven",
			"Ishaan",
			"Lyric",
			"Ulises",
			"Jamir",
			"Marcelo",
			"Davin",
			"Bodhi",
			"Justus",
			"Mack",
			"Rudy",
			"Cedric",
			"Craig",
			"Frankie",
			"Javion",
			"Maxton",
			"Deshawn",
			"Jair",
			"Duncan",
			"Hassan",
			"Gibson",
			"Isiah",
			"Cayson",
			"Darwin",
			"Kale",
			"Kolten",
			"Lucca",
			"Kase",
			"Konner",
			"Konnor",
			"Randall",
			"Azariah",
			"Stefan",
			"Enoch",
			"Kymani",
			"Dominique",
			"Maximo",
			"Van",
			"Forrest",
			"Alvaro",
			"Gannon",
			"Jordyn",
			"Rolando",
			"Sonny",
			"Brice",
			"Coleman",
			"Yousef",
			"Aydan",
			"Ean",
			"Johnathon",
			"Quintin",
			"Semaj",
			"Cristopher",
			"Harlan",
			"Vaughn",
			"Zeke",
			"Axton",
			"Damion",
			"Jovanni",
			"Fisher",
			"Heath",
			"Ramiro",
			"Seamus",
			"Vance",
			"Yael",
			"Jadon",
			"Kamdyn",
			"Rashad",
			"Camdyn",
			"Jedidiah",
			"Santos",
			"Steve",
			"Chace",
			"Marley",
			"Brecken",
			"Kamryn",
			"Valentin",
			"Dilan",
			"Mike",
			"Krish",
			"Salvatore",
			"Brantlee",
			"Gilbert",
			"Turner",
			"Camren",
			"Franco",
			"Hezekiah",
			"Zaid",
			"Anders",
			"Deangelo",
			"Harold",
			"Joziah",
			"Mustafa",
			"Emory",
			"Jamar",
			"Reuben",
			"Royal",
			"Zayn",
			"Arnav",
			"Bently",
			"Gavyn",
			"Ares",
			"Ameer",
			"Juelz",
			"Rodolfo",
			"Titan",
			"Bridger",
			"Briggs",
			"Cortez",
			"Blaise",
			"Demarcus",
			"Rey",
			"Hugh",
			"Benton",
			"Giovanny",
			"Tristin",
			"Aidyn",
			"Jovani",
			"Jaylin",
			"Jorden",
			"Kaeden",
			"Clinton",
			"Efrain",
			"Kingsley",
			"Makhi",
			"Aditya",
			"Teagan",
			"Jericho",
			"Kamron",
			"Xavi",
			"Ernest",
			"Kaysen",
			"Zaire",
			"Deon",
			"Foster",
			"Lochlan",
			"Gilberto",
			"Gino",
			"Izayah",
			"Maison",
			"Miller",
			"Antoine",
			"Garrison",
			"Rylee",
			"Cristiano",
			"Dangelo",
			"Keenan",
			"Stetson",
			"Truman",
			"Brysen",
			"Jaycob",
			"Kohen",
			"Augustine",
			"Castiel",
			"Langston",
			"Magnus",
			"Osvaldo",
			"Reagan",
			"Sidney",
			"Tyree",
			"Yair",
			"Deegan",
			"Kalel",
			"Todd",
			"Alfred",
			"Anson",
			"Apollo",
			"Rowen",
			"Santana",
			"Ephraim",
			"Houston",
			"Jayse",
			"Leroy",
			"Pierre",
			"Tyrell",
			"Camryn",
			"Grey",
			"Yadiel",
			"Aaden",
			"Corban",
			"Denzel",
			"Jordy",
			"Kannon",
			"Branden",
			"Brendon",
			"Brenton",
			"Dario",
			"Jakobe",
			"Lachlan",
			"Thatcher",
			"Immanuel",
			"Camilo",
			"Davon",
			"Graeme",
			"Rocky",
			"Broderick",
			"Clyde",
			"Darien"
	};
	
	static String[] girlNames = new String[]{
			"Sophia",
			"Emma",
			"Olivia",
			"Isabella",
			"Ava",
			"Mia",
			"Emily",
			"Abigail",
			"Madison",
			"Elizabeth",
			"Charlotte",
			"Avery",
			"Sofia",
			"Chloe",
			"Ella",
			"Harper",
			"Amelia",
			"Aubrey",
			"Addison",
			"Evelyn",
			"Natalie",
			"Grace",
			"Hannah",
			"Zoey",
			"Victoria",
			"Lillian",
			"Lily",
			"Brooklyn",
			"Samantha",
			"Layla",
			"Zoe",
			"Audrey",
			"Leah",
			"Allison",
			"Anna",
			"Aaliyah",
			"Savannah",
			"Gabriella",
			"Camila",
			"Aria",
			"Kaylee",
			"Scarlett",
			"Hailey",
			"Arianna",
			"Riley",
			"Alexis",
			"Nevaeh",
			"Sarah",
			"Claire",
			"Sadie",
			"Peyton",
			"Aubree",
			"Serenity",
			"Ariana",
			"Genesis",
			"Penelope",
			"Alyssa",
			"Bella",
			"Taylor",
			"Alexa",
			"Kylie",
			"Mackenzie",
			"Caroline",
			"Kennedy",
			"Autumn",
			"Lucy",
			"Ashley",
			"Madelyn",
			"Violet",
			"Stella",
			"Brianna",
			"Maya",
			"Skylar",
			"Ellie",
			"Julia",
			"Sophie",
			"Katherine",
			"Mila",
			"Khloe",
			"Paisley",
			"Annabelle",
			"Alexandra",
			"Nora",
			"Melanie",
			"London",
			"Gianna",
			"Naomi",
			"Eva",
			"Faith",
			"Madeline",
			"Lauren",
			"Nicole",
			"Ruby",
			"Makayla",
			"Kayla",
			"Lydia",
			"Piper",
			"Sydney",
			"Jocelyn",
			"Morgan",
			"Kimberly",
			"Molly",
			"Jasmine",
			"Reagan",
			"Bailey",
			"Eleanor",
			"Alice",
			"Trinity",
			"Rylee",
			"Andrea",
			"Hadley",
			"Maria",
			"Brooke",
			"Mariah",
			"Isabelle",
			"Brielle",
			"Mya",
			"Quinn",
			"Vivian",
			"Natalia",
			"Mary",
			"Liliana",
			"Payton",
			"Lilly",
			"Eliana",
			"Jade",
			"Cora",
			"Paige",
			"Valentina",
			"Kendall",
			"Clara",
			"Elena",
			"Jordyn",
			"Kaitlyn",
			"Delilah",
			"Isabel",
			"Destiny",
			"Rachel",
			"Amy",
			"Mckenzie",
			"Gabrielle",
			"Brooklynn",
			"Katelyn",
			"Laila",
			"Aurora",
			"Ariel",
			"Angelina",
			"Aliyah",
			"Juliana",
			"Vanessa",
			"Adriana",
			"Ivy",
			"Lyla",
			"Sara",
			"Willow",
			"Reese",
			"Hazel",
			"Eden",
			"Elise",
			"Josephine",
			"Kinsley",
			"Ximena",
			"Jessica",
			"Londyn",
			"Makenzie",
			"Gracie",
			"Isla",
			"Michelle",
			"Valerie",
			"Kylee",
			"Melody",
			"Catherine",
			"Adalynn",
			"Jayla",
			"Alexia",
			"Valeria",
			"Adalyn",
			"Rebecca",
			"Izabella",
			"Alaina",
			"Margaret",
			"Alana",
			"Alivia",
			"Kate",
			"Luna",
			"Norah",
			"Kendra",
			"Summer",
			"Ryleigh",
			"Julianna",
			"Jennifer",
			"Lila",
			"Hayden",
			"Emery",
			"Stephanie",
			"Angela",
			"Fiona",
			"Daisy",
			"Presley",
			"Eliza",
			"Harmony",
			"Melissa",
			"Giselle",
			"Keira",
			"Kinley",
			"Alayna",
			"Alexandria",
			"Emilia",
			"Marley",
			"Arabella",
			"Emerson",
			"Adelyn",
			"Brynn",
			"Lola",
			"Leila",
			"Mckenna",
			"Aniyah",
			"Athena",
			"Genevieve",
			"Allie",
			"Gabriela",
			"Daniela",
			"Cecilia",
			"Rose",
			"Adrianna",
			"Callie",
			"Jenna",
			"Esther",
			"Haley",
			"Leilani",
			"Maggie",
			"Adeline",
			"Hope",
			"Jaylah",
			"Amaya",
			"Maci",
			"Ana",
			"Juliet",
			"Jacqueline",
			"Charlie",
			"Lucia",
			"Tessa",
			"Camille",
			"Katie",
			"Miranda",
			"Lexi",
			"Makenna",
			"Jada",
			"Delaney",
			"Cassidy",
			"Alina",
			"Georgia",
			"Iris",
			"Ashlyn",
			"Kenzie",
			"Megan",
			"Anastasia",
			"Paris",
			"Shelby",
			"Jordan",
			"Danielle",
			"Lilliana",
			"Sienna",
			"Teagan",
			"Josie",
			"Angel",
			"Parker",
			"Mikayla",
			"Brynlee",
			"Diana",
			"Chelsea",
			"Kathryn",
			"Erin",
			"Annabella",
			"Kaydence",
			"Lyric",
			"Arya",
			"Madeleine",
			"Kayleigh",
			"Vivienne",
			"Sabrina",
			"Cali",
			"Raelynn",
			"Leslie",
			"Kyleigh",
			"Ayla",
			"Nina",
			"Amber",
			"Daniella",
			"Finley",
			"Olive",
			"Miriam",
			"Dakota",
			"Elliana",
			"Juliette",
			"Noelle",
			"Alison",
			"Amanda",
			"Alessandra",
			"Evangeline",
			"Phoebe",
			"Bianca",
			"Christina",
			"Yaretzi",
			"Raegan",
			"Kelsey",
			"Lilah",
			"Fatima",
			"Kiara",
			"Elaina",
			"Cadence",
			"Nyla",
			"Addyson",
			"Giuliana",
			"Alondra",
			"Gemma",
			"Ashlynn",
			"Carly",
			"Kyla",
			"Alicia",
			"Adelaide",
			"Laura",
			"Allyson",
			"Charlee",
			"Nadia",
			"Mallory",
			"Heaven",
			"Cheyenne",
			"Ruth",
			"Tatum",
			"Lena",
			"Ainsley",
			"Amiyah",
			"Journey",
			"Malia",
			"Haylee",
			"Veronica",
			"Eloise",
			"Myla",
			"Mariana",
			"Jillian",
			"Joanna",
			"Madilyn",
			"Baylee",
			"Selena",
			"Briella",
			"Sierra",
			"Rosalie",
			"Gia",
			"Briana",
			"Talia",
			"Abby",
			"Heidi",
			"Annie",
			"Jane",
			"Maddison",
			"Kira",
			"Carmen",
			"Lucille",
			"Harley",
			"Macy",
			"Skyler",
			"Kali",
			"June",
			"Elsie",
			"Kamila",
			"Adelynn",
			"Arielle",
			"Kelly",
			"Scarlet",
			"Rylie",
			"Haven",
			"Marilyn",
			"Aubrie",
			"Kamryn",
			"Kara",
			"Hanna",
			"Averie",
			"Marissa",
			"Jayda",
			"Jazmine",
			"Camryn",
			"Everly",
			"Jazmin",
			"Lia",
			"Karina",
			"Maliyah",
			"Miley",
			"Bethany",
			"Mckinley",
			"Jayleen",
			"Esmeralda",
			"Macie",
			"Aleah",
			"Catalina",
			"Nayeli",
			"Daphne",
			"Janelle",
			"Camilla",
			"Madelynn",
			"Kyra",
			"Addisyn",
			"Aylin",
			"Julie",
			"Caitlyn",
			"Sloane",
			"Gracelyn",
			"Elle",
			"Helen",
			"Michaela",
			"Serena",
			"Lana",
			"Angelica",
			"Raelyn",
			"Nylah",
			"Karen",
			"Emely",
			"Bristol",
			"Sarai",
			"Alejandra",
			"Brittany",
			"Vera",
			"April",
			"Francesca",
			"Logan",
			"Rowan",
			"Skye",
			"Sasha",
			"Carolina",
			"Kassidy",
			"Miracle",
			"Ariella",
			"Tiffany",
			"Itzel",
			"Justice",
			"Ada",
			"Brylee",
			"Jazlyn",
			"Dahlia",
			"Julissa",
			"Kaelyn",
			"Savanna",
			"Kennedi",
			"Anya",
			"Viviana",
			"Cataleya",
			"Jayden",
			"Sawyer",
			"Holly",
			"Kaylie",
			"Blakely",
			"Kailey",
			"Jimena",
			"Melany",
			"Emmalyn",
			"Guadalupe",
			"Sage",
			"Annalise",
			"Cassandra",
			"Madisyn",
			"Anabelle",
			"Kaylin",
			"Amira",
			"Crystal",
			"Elisa",
			"Caitlin",
			"Lacey",
			"Rebekah",
			"Celeste",
			"Danna",
			"Marlee",
			"Gwendolyn",
			"Joselyn",
			"Karla",
			"Joy",
			"Audrina",
			"Janiyah",
			"Anaya",
			"Malaysia",
			"Annabel",
			"Kadence",
			"Zara",
			"Imani",
			"Maeve",
			"Priscilla",
			"Phoenix",
			"Aspen",
			"Katelynn",
			"Dylan",
			"Eve",
			"Jamie",
			"Lexie",
			"Jaliyah",
			"Kailyn",
			"Lilian",
			"Braelyn",
			"Angie",
			"Lauryn",
			"Cynthia",
			"Emersyn",
			"Lorelei",
			"Monica",
			"Alanna",
			"Brinley",
			"Sylvia",
			"Journee",
			"Nia",
			"Aniya",
			"Breanna",
			"Fernanda",
			"Lillie",
			"Amari",
			"Charley",
			"Lilyana",
			"Luciana",
			"Raven",
			"Kaliyah",
			"Emilee",
			"Anne",
			"Bailee",
			"Hallie",
			"Zariah",
			"Bridget",
			"Annika",
			"Gloria",
			"Zuri",
			"Madilynn",
			"Elsa",
			"Nova",
			"Kiley",
			"Johanna",
			"Liberty",
			"Rosemary",
			"Aleena",
			"Courtney",
			"Madalyn",
			"Aryanna",
			"Tatiana",
			"Angelique",
			"Harlow",
			"Leighton",
			"Hayley",
			"Skyla",
			"Kenley",
			"Tiana",
			"Dayana",
			"Evelynn",
			"Selah",
			"Helena",
			"Blake",
			"Virginia",
			"Cecelia",
			"Nathalie",
			"Jaycee",
			"Danica",
			"Dulce",
			"Gracelynn",
			"Ember",
			"Evie",
			"Anika",
			"Emilie",
			"Erica",
			"Tenley",
			"Anabella",
			"Liana",
			"Cameron",
			"Braylee",
			"Aisha",
			"Charleigh",
			"Hattie",
			"Leia",
			"Lindsey",
			"Marie",
			"Regina",
			"Isis",
			"Alyson",
			"Anahi",
			"Elyse",
			"Felicity",
			"Jaelyn",
			"Amara",
			"Natasha",
			"Samara",
			"Lainey",
			"Daleyza",
			"Miah",
			"Melina",
			"River",
			"Amani",
			"Aileen",
			"Jessie",
			"Whitney",
			"Beatrice",
			"Caylee",
			"Greta",
			"Jaelynn",
			"Milan",
			"Millie",
			"Lea",
			"Marina",
			"Kaylynn",
			"Kenya",
			"Mariam",
			"Amelie",
			"Kaia",
			"Maleah",
			"Ally",
			"Colette",
			"Elisabeth",
			"Dallas",
			"Erika",
			"Karlee",
			"Alayah",
			"Alani",
			"Farrah",
			"Bria",
			"Madalynn",
			"Mikaela",
			"Adelina",
			"Amina",
			"Cara",
			"Jaylynn",
			"Leyla",
			"Nataly",
			"Braelynn",
			"Kiera",
			"Laylah",
			"Paislee",
			"Desiree",
			"Malaya",
			"Azalea",
			"Kensley",
			"Shiloh",
			"Brenda",
			"Lylah",
			"Addilyn",
			"Amiya",
			"Amya",
			"Maia",
			"Irene",
			"Ryan",
			"Jasmin",
			"Linda",
			"Adele",
			"Matilda",
			"Emelia",
			"Emmy",
			"Juniper",
			"Saige",
			"Ciara",
			"Estrella",
			"Jaylee",
			"Jemma",
			"Meredith",
			"Myah",
			"Rosa",
			"Teresa",
			"Yareli",
			"Kimber",
			"Madyson",
			"Claudia",
			"Maryam",
			"Zoie",
			"Kathleen",
			"Mira",
			"Paityn",
			"Isabela",
			"Perla",
			"Sariah",
			"Sherlyn",
			"Paola",
			"Shayla",
			"Winter",
			"Mae",
			"Simone",
			"Laney",
			"Pearl",
			"Ansley",
			"Jazlynn",
			"Patricia",
			"Aliana",
			"Brenna",
			"Armani",
			"Giana",
			"Lindsay",
			"Natalee",
			"Lailah",
			"Siena",
			"Nancy",
			"Raquel",
			"Willa",
			"Lilianna",
			"Frances",
			"Halle",
			"Janessa",
			"Kynlee",
			"Tori",
			"Leanna",
			"Bryanna",
			"Ellen",
			"Alma",
			"Lizbeth",
			"Wendy",
			"Chaya",
			"Christine",
			"Elianna",
			"Mabel",
			"Clarissa",
			"Kassandra",
			"Mollie",
			"Charli",
			"Diamond",
			"Kristen",
			"Coraline",
			"Mckayla",
			"Ariah",
			"Arely",
			"Blair",
			"Edith",
			"Joslyn",
			"Hailee",
			"Jaylene",
			"Chanel",
			"Alia",
			"Reyna",
			"Casey",
			"Clare",
			"Dana",
			"Alena",
			"Averi",
			"Alissa",
			"Demi",
			"Aiyana",
			"Leona",
			"Kailee",
			"Karsyn",
			"Kallie",
			"Taryn",
			"Corinne",
			"Rayna",
			"Asia",
			"Jaylin",
			"Noemi",
			"Carlee",
			"Abbigail",
			"Aryana",
			"Ayleen",
			"Eileen",
			"Livia",
			"Lillianna",
			"Mara",
			"Danika",
			"Mina",
			"Aliya",
			"Paloma",
			"Aimee",
			"Kaya",
			"Kora",
			"Tabitha",
			"Denise",
			"Hadassah",
			"Kayden",
			"Monroe",
			"Briley",
			"Celia",
			"Sandra",
			"Elaine",
			"Hana",
			"Jolie",
			"Kristina",
			"Myra",
			"Milana",
			"Lisa",
			"Renata",
			"Zariyah",
			"Adrienne",
			"America",
			"Emmalee",
			"Zaniyah",
			"Celine",
			"Cherish",
			"Jaida",
			"Kimora",
			"Mariyah",
			"Avah",
			"Nola",
			"Iliana",
			"Chana",
			"Cindy",
			"Janiya",
			"Carolyn",
			"Marisol",
			"Maliah",
			"Galilea",
			"Kiana",
			"Milania",
			"Alaya",
			"Bryn",
			"Emory",
			"Lorelai",
			"Jocelynn",
			"Yamileth",
			"Martha",
			"Jenny",
			"Keyla",
			"Alyvia",
			"Wren",
			"Dorothy",
			"Jordynn",
			"Amirah",
			"Nathaly",
			"Taliyah",
			"Zaria",
			"Deborah",
			"Elin",
			"Rylan",
			"Aubrianna",
			"Yasmin",
			"Julianne",
			"Zion",
			"Roselyn",
			"Salma",
			"Ivanna",
			"Joyce",
			"Paulina",
			"Lilith",
			"Saniyah",
			"Janae",
			"Aubrielle",
			"Ayanna",
			"Henley",
			"Sutton",
			"Aurelia",
			"Lesly",
			"Remi",
			"Britney",
			"Heather",
			"Barbara",
			"Bryleigh",
			"Emmalynn",
			"Kaitlynn",
			"Elliot",
			"Milena",
			"Susan",
			"Ariyah",
			"Kyndall",
			"Paula",
			"Thalia",
			"Aubri",
			"Kaleigh",
			"Tegan",
			"Yaritza",
			"Angeline",
			"Mercy",
			"Kairi",
			"Kourtney",
			"Krystal",
			"Carla",
			"Carter",
			"Mercedes",
			"Alannah",
			"Lina",
			"Sonia",
			"Kenia",
			"Everleigh",
			"Ivory",
			"Sloan",
			"Abril",
			"Alisha",
			"Katalina",
			"Carlie",
			"Lara",
			"Laurel",
			"Scarlette",
			"Carley",
			"Dixie",
			"Miya",
			"Micah",
			"Regan",
			"Samiyah",
			"Charlize",
			"Sharon",
			"Rosie",
			"Aviana",
			"Aleigha",
			"Gwyneth",
			"Sky",
			"Estella",
			"Hadlee",
			"Luz",
			"Patience",
			"Temperance",
			"Ingrid",
			"Raina",
			"Libby",
			"Jurnee",
			"Zahra",
			"Belen",
			"Jewel",
			"Anabel",
			"Marianna",
			"Renee",
			"Rory",
			"Elliott",
			"Karlie",
			"Saylor",
			"Deanna",
			"Freya",
			"Lilia",
			"Marjorie",
			"Sidney",
			"Tara",
			"Azaria",
			"Campbell",
			"Kai",
			"Ann",
			"Destinee",
			"Ariya",
			"Lilyanna",
			"Avianna",
			"Macey",
			"Shannon",
			"Lennon",
			"Saniya",
			"Haleigh",
			"Jolene",
			"Liv",
			"Oakley",
			"Esme",
			"Hunter",
			"Aliza",
			"Amalia",
			"Annalee",
			"Evalyn",
			"Giavanna",
			"Karis",
			"Kaylen",
			"Rayne",
			"Audriana",
			"Emerie",
			"Giada",
			"Harlee",
			"Kori",
			"Margot",
			"Abrielle",
			"Ellison",
			"Gwen",
			"Moriah",
			"Wynter",
			"Alisson",
			"Belinda",
			"Cristina",
			"Lillyana",
			"Neriah",
			"Rihanna",
			"Tamia",
			"Rivka",
			"Annabell",
			"Araceli",
			"Ayana",
			"Emmaline",
			"Giovanna",
			"Kylah",
			"Kailani",
			"Karissa",
			"Nahla",
			"Zainab",
			"Devyn",
			"Karma",
			"Marleigh",
			"Meadow",
			"India",
			"Kaiya",
			"Sarahi",
			"Audrianna",
			"Natalya",
			"Bayleigh",
			"Estelle",
			"Kaidence",
			"Kaylyn",
			"Magnolia",
			"Princess",
			"Avalyn",
			"Ireland",
			"Jayde",
			"Roxanne",
			"Alaysia",
			"Amia",
			"Astrid",
			"Karly",
			"Dalilah",
			"Makena",
			"Penny",
			"Ryann",
			"Charity",
			"Judith",
			"Kenna",
			"Tess",
			"Tinley",
			"Collins"
	};
	

	static{
		init();
	}

}
