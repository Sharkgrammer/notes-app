<?php
	
$con = mysqli_connect("den1.mysql1.gear.host","note","Qr7I5!96q!69", "note");
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
$auth_key = explode(",", $dbstring)[1];
$user_id = explode(",", $dbstring)[0];

$type = $_GET['type'];

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
			//$type = 0;
			//echo ":(";
		}
		else
		{
			while($row = mysqli_fetch_assoc($result)){
				if ($auth_key != $row['user_key']){
					//$type = 0;
				}
			}
		}
	}
}

if ($type == 1){
	//Login function
	$username = $con->real_escape_string(htmlspecialchars($_GET['username']));
	$password = $con->real_escape_string(htmlspecialchars($_GET['password']));
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
					$key_string = generateRandomString(50);
					$temp_id = $row['user_id'];
					$query2 = "update users set user_key = '$key_string' where user_id = '$temp_id';";
					mysqli_query($con, $query2) or die("Error");
					
					echo $row['user_id'];
					echo "---";
					echo $row['user_name'];
					echo "---";
					echo $key_string;
				}
			}
		}
	} else {
		echo "Error";
	}
}elseif($type == 2){
	$user_email = $con->real_escape_string(htmlspecialchars($_GET['emai']));
	$user_pass = $con->real_escape_string(htmlspecialchars($_GET['pass']));
	$hashed_password = password_hash($user_pass, PASSWORD_DEFAULT);
	
	$query = "insert into users (user_email, user_pass)
	values('$user_email', '$hashed_password');";
	mysqli_query($con, $query) or die(mysqli_error($con));
	echo "Table Updated";
}elseif($type == 3){
	$id = $con->real_escape_string(htmlspecialchars($_GET['id']));
	$title = $con->real_escape_string(htmlspecialchars($_GET['title']));
	$content = $con->real_escape_string(htmlspecialchars($_GET['content']));
	$type = $con->real_escape_string(htmlspecialchars($_GET['type']));
	
	$query = "insert into note (user_id, note_title, note_content, note_date, note_type)
	values('$id', '$title', '$content', NOW(), '$type');";
	mysqli_query($con, $query) or die(mysqli_error($con));
	
	echo "Table Updated";
}elseif($type == 4){
	$id = $con->real_escape_string(htmlspecialchars($_GET['ID']));
	$query = "select * from note where user_id = '$id' order by note_id";
	
	$result = mysqli_query($con, $query) or die(mysqli_error($con));		
	if(!$result)
	{
		echo 'Empty posts' . mysqli_error();
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
				echo "---";
				echo $row['user_id'];
				echo "---";
				echo $row['note_title'];
				echo "---";
				echo $row['note_content'];
				echo "---";
				echo $row['note_date'];
				echo "---";
				echo $row['note_type'];
				echo ";";
			}
		}
	}
}elseif($type == 5){
	$id = $con->real_escape_string(htmlspecialchars($_GET['ID']));
	$query = "delete from note where note_id = '$id'";
	mysqli_query($con, $query) or die(mysqli_error($con));
	
	echo "deleted";		
}else{
	echo "Error 404: Error not found";
}
mysqli_close($con);
?>