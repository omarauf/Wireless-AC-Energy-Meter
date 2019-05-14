<?php
    require_once 'include/DB_Functions.php';
    $db = new DB_Functions();

    if (isset($_POST['code'])) {    // edit by omar
        
        // receiving the post params from url
        $code = $_POST['code'];

        if ($code === "getData"){
            $response["Days"] = $db->getDayData();
            $response["Months"] = $db->getMonthData();
            $response["Weeks"] = $db->getWeekData();
            $response["Periods"] = $db->getPeriodData();
            echo json_encode($response);
        } else if ($code === "getState"){
            $response["Start"] = $db->getStart();
            $response["End"] = $db->getEnd();
            $response["State"] = $db->getState();
            echo json_encode($response);

        }
    }  
?>
<!doctype html>
<html lang="en">

<head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <!-- Bootstrap CSS -->
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css" integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO"
        crossorigin="anonymous">

    <title>Login</title>
</head>

<body>


    <div class="container">
        <h1>Get in touch!</h1>
        <form method="post">
            <!--Emain Address-->
            <div class="form-group">
                <label for="code">code</label>
                <input type="code" class="form-control" id="code" name="code" aria-describedby="emailHelp" placeholder="getData">
            </div>
            <button type="submit" id="submit" class="btn btn-primary">Submit</button>
        </form>
    </div>


    <!-- Optional JavaScript -->
    <!-- jQuery first, then Bootstrap JS -->
    <script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js" integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy" crossorigin="anonymous"></script>

</body>

</html>