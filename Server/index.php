<?php

$creds = fgets(fopen("dbcreds.txt", "r")) or die("Unable to open file");

$loc = explode(";", $creds)[0];
$user = explode(";", $creds)[1];
$pass = explode(";", $creds)[2];
fclose($creds);

$con = mysqli_connect($loc,$user,$pass,$user);
if (mysqli_connect_errno($con)) {
    echo "Failed to connect to MySQL: " . mysqli_connect_error();
}

//Function generates a auth key
function generateRandomString($length) {
	global $con;
    $characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ';
    $charactersLength = strlen($characters);
    $randomString = ''; $tempString = ""; $control = false;
	
    for ($i = 0; $i < $length; $i++) {
        $randomString .= $characters[rand(0, $charactersLength - 1)];
    }
	
	$query = "select user_key from users";
	$result = mysqli_query($con, $query) or die('Error querying database');
	if(!$result){
		echo 'Nope' . mysqli_error();
	}
	else
	{
		if(mysqli_num_rows($result) == 0)
		{
			echo "Error";
		}
		else
		{
			while($row = mysqli_fetch_assoc($result))
			{
				if ($randomString == $row['user_key']){
					$control = true;
					break;
				}
			}
		}
	}

	if ($control != true){
		$tempString = $randomString;
	}else{
		$tempString = generateRandomString(50);
	}
	
    return $tempString;
}

$dbstring = $_GET['key'];
if (strlen($dbstring) < 1){
	$dbstring = $_POST['key'];
}

$auth_key = explode(";", $dbstring)[1];
$user_id = explode(";", $dbstring)[0];

$type = $_GET['type'];
if (strlen($type) < 1){
	$type = $_POST['type'];
}

//keys are checked here
if($type > 2){
	$query = "select user_key from users where user_id = '$user_id'";
	$result = mysqli_query($con, $query) or die('Error querying database');
	if(!$result){
		echo 'Nope' . mysqli_error();
	}
	else
	{
		if(mysqli_num_rows($result) == 0)
		{
			$type = 0;
			//echo ":(";
		}
		else
		{
			while($row = mysqli_fetch_assoc($result)){
				if ($auth_key != $row['user_key']){
					$type = 0;
				}
			}
		}
	}
}

if ($type == 1){
	//Login function
	$username = $con->real_escape_string(htmlspecialchars($_POST['username']));
	$password = $con->real_escape_string(htmlspecialchars($_POST['password']));
	$query2 = "select user_pass from users where user_email = '$username';";
	$result2 = mysqli_query($con, $query2) or die(mysqli_error($con));	
	$row2 = mysqli_fetch_assoc($result2);
	$hash = $row2['user_pass'];
	if (password_verify($password, $hash)) {
		$query = "select * from users where user_email='$username' and user_pass='$hash';";
		$result = mysqli_query($con, $query) or die('Error querying database');		
		if(!$result)
		{
			echo 'Nope' . mysqli_error();
		}
		else
		{
			if(mysqli_num_rows($result) == 0)
			{
				echo "Error";
			}
			else
			{
				while($row = mysqli_fetch_assoc($result))
				{
					
					$temp_key = $row['user_key'];
					$temp_id = $row['user_id'];
					echo $temp_id;
					echo ";";
					if (strlen($temp_key) < 3){
						$key_string = generateRandomString(50);
						$query2 = "update users set user_key = '$key_string' where user_id = '$temp_id';";
						mysqli_query($con, $query2) or die("Error");
						echo $key_string;						
					}else{
						echo $temp_key;
					}
				}
			}
		}
	} else {
		echo "Error";
	}
}elseif($type == 2){
	$user_email = $con->real_escape_string(htmlspecialchars($_POST['username']));
	
	$query = "select user_email from users where user_email LIKE '$user_email'";
	$result = mysqli_query($con, $query) or die('Error querying database');		
	if(!$result)
	{
		echo 'Empty search' . mysqli_error();
	}
	else
	{
		if(mysqli_num_rows($result) == 0)
		{
			$user_pass = $con->real_escape_string(htmlspecialchars($_POST['password']));
			$hashed_password = password_hash($user_pass, PASSWORD_DEFAULT);
	
			$query2 = "insert into users (user_email, user_pass)
			values('$user_email', '$hashed_password');";
			mysqli_query($con, $query2) or die(mysqli_error($con));
			echo "Table Updated";
		}
		else
		{
			echo "Error";
		}
	}
}elseif($type == 3){
	$id = $con->real_escape_string(htmlspecialchars($_POST['ID']));
	$title = $con->real_escape_string(htmlspecialchars($_POST['title']));
	$content = $con->real_escape_string(htmlspecialchars($_POST['content']));
	$content = str_replace("\\n", "/para/", $content);
	$type = $con->real_escape_string(htmlspecialchars($_POST['ntype']));
	$query = "insert into note (user_id, note_title, note_content, note_date, note_type, theme_id)
	values('$id', '$title', '$content', NOW(), '$type', 1);";
	mysqli_query($con, $query) or die(mysqli_error($con));
	
	$query3 = "select MAX(note_id) from note";
	$result3 = mysqli_query($con, $query3) or die(mysqli_error($con));	
	$row3 = mysqli_fetch_assoc($result3);
	$num = $row3['MAX(note_id)'];
	
	echo $num;
}elseif($type == 4){
	$id = $con->real_escape_string(htmlspecialchars($_GET['ID']));
	$query = "select * from note where user_id = '$id' order by note_id";
	
	$result = mysqli_query($con, $query) or die(mysqli_error($con));		
	if(!$result)
	{
		echo 'Empty notes' . mysqli_error();
	}
	else
	{
		if(mysqli_num_rows($result) == 0)
		{
			echo "Error";
		}
		else
		{
			while($row = mysqli_fetch_assoc($result))
			{
				echo $row['note_id'];
				echo "/split1/";
				echo $row['user_id'];
				echo "/split1/";
				echo $row['note_title'];
				echo "/split1/";
				echo $row['note_content'];
				echo "/split1/";
				echo $row['note_date'];
				echo "/split1/";
				echo $row['note_type'];
				echo "/split1/";
				echo $row['theme_id'];
				echo "/split2/";
			}
		}
	}
}elseif($type == 5){
	$id = $con->real_escape_string(htmlspecialchars($_POST['ID']));
	$query = "delete from note where note_id = '$id'";
	mysqli_query($con, $query) or die(mysqli_error($con));
	
	echo "deleted";	
}elseif($type == 6){
	$id = $con->real_escape_string(htmlspecialchars($_POST['ID']));
	$title = $con->real_escape_string(htmlspecialchars($_POST['title']));
	$content = $con->real_escape_string(htmlspecialchars($_POST['content']));
	$content = str_replace("\\n", "/para/", $content);
	echo $content;
	$type = $con->real_escape_string(htmlspecialchars($_POST['ntype']));
	$query = "update note set note_title = '$title', 
	note_content = '$content', note_type = '$type' where note_id = '$id'";
	mysqli_query($con, $query) or die(mysqli_error($con));
}elseif($type == 7){
	$query = "select * from theme";
	
	$result = mysqli_query($con, $query) or die(mysqli_error($con));		
	if(!$result)
	{
		echo 'Empty themes ' . mysqli_error();
	}
	else
	{
		if(mysqli_num_rows($result) == 0)
		{
			echo "Error";
		}
		else
		{
			while($row = mysqli_fetch_assoc($result))
			{
				echo $row['theme_id'];
				echo ",";
				echo $row['theme_name'];
				echo ",";
				echo $row['prim_col'];
				echo ",";
				echo $row['seco_col'];
				echo ",";
				echo $row['text_col'];
				echo ",";
				echo $row['hint_col'];
				echo ",";
				echo $row['acce_col'];
				echo ",";
				echo $row['but_col'];
				echo ";";
			}
		}
	}
}elseif($type == 8){
	$note = $con->real_escape_string(htmlspecialchars($_POST['note']));
	$theme = $con->real_escape_string(htmlspecialchars($_POST['theme']));
	$query = "update note set theme_id = '$theme' where note_id = '$note'";
	mysqli_query($con, $query) or die(mysqli_error($con));
}else{
	echo "Authentication/Mismatch error";
}
mysqli_close($con);
?>