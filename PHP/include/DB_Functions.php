<?php
 
/**
 * @author Ravi Tamada
 * @link https://www.androidhive.info/2012/01/android-login-and-registration-with-php-mysql-and-sqlite/ Complete tutorial
 */
 
class DB_Functions {
 
    private $conn;
 
    // constructor
    function __construct() {
        require_once 'DB_Connect.php';
        // connecting to database
        $db = new Db_Connect();
        $this->conn = $db->connect();
    }
 
    // destructor
    function __destruct() {
         
    }

    /**
     * Get Days data
     */
     public function getDayData() {
        $stms = $this->conn->prepare("SELECT `Energy`, `Power`, `Current`, `Voltage`, `Power_Factor`, `Day` FROM `Days` ORDER BY `Day` ASC");
        if ($stms->execute()) {
            $result = $stms->get_result();
			while ($datas = $result->fetch_assoc()) {
                //echo json_encode($datas);
                //echo "<br />";
				$data[] = $datas;
			}
            $stms->close(); 
			return $data;
         } 
    }

    /**
     * Get Months data
     */
     public function getMonthData() {
        $stms = $this->conn->prepare("SELECT `Energy`, `Power`, `Current`, `Voltage`, `Power_Factor`, `Month` FROM `Months` ORDER BY `Month` ASC");
        if ($stms->execute()) {
            $result = $stms->get_result();
			while ($datas = $result->fetch_assoc()) {
                //echo json_encode($datas);
                //echo "<br />";
				$data[] = $datas;
			}
            $stms->close(); 
			return $data;
         } 
    }

    /**
     * Get Weeks data
     */
    public function getWeekData() {
        $stms = $this->conn->prepare("SELECT `Energy`, `Power`, `Current`, `Voltage`, `Power_Factor`, `Week` FROM `Weeks` ORDER BY `Week` ASC");
        if ($stms->execute()) {
            $result = $stms->get_result();
			while ($datas = $result->fetch_assoc()) {
                //echo json_encode($datas);
                //echo "<br />";
				$data[] = $datas;
			}
            $stms->close(); 
			return $data;
         } 
    }

    /**
     * Get Period data
     */
     public function getPeriodData() {
        $stms = $this->conn->prepare("SELECT `Energy`, `Power`, `Current`, `Voltage`, `Power_Factor`, `Day`, `Period` FROM `Period` ORDER BY `Day` ASC");
        if ($stms->execute()) {
            $result = $stms->get_result();
			while ($datas = $result->fetch_assoc()) {
                //echo json_encode($datas);
                //echo "<br />";
				$data[] = $datas;
			}
            $stms->close(); 
			return $data;
         } 
    }

    /**
     * Get State data
     */
     public function getStart() {
        $stms = $this->conn->prepare("SELECT * FROM `Start`");
        if ($stms->execute()) {
            $result = $stms->get_result();
            $datas = $result->fetch_assoc();
			/*while ($datas = $result->fetch_assoc()) {
                //echo json_encode($datas);
                //echo "<br />";
				$data[] = $datas;
			}*/
            $stms->close(); 
			return $datas;
         } 
    }

    /**
     * Get State data
     */
     public function getEnd() {
        $stms = $this->conn->prepare("SELECT * FROM `End`");
        if ($stms->execute()) {
            $result = $stms->get_result();
            $datas = $result->fetch_assoc();
			/*while ($datas = $result->fetch_assoc()) {
                //echo json_encode($datas);
                //echo "<br />";
				$data[] = $datas;
			}*/
            $stms->close(); 
			return $datas;
         } 
    }

    /**
     * Get State data
     */
     public function getState() {
        $stms = $this->conn->prepare("SELECT * FROM `State`");
        if ($stms->execute()) {
            $result = $stms->get_result();
            $datas = $result->fetch_assoc();
			/*while ($datas = $result->fetch_assoc()) {
                //echo json_encode($datas);
                //echo "<br />";
				$data[] = $datas;
			}*/
            $stms->close(); 
			return $datas;
         } 
    }

    
		
	/**
     * get notes to update list view note in Note.php
     * noteIDToBeActive == -1 activate first Note
     */
    public function updateNoteListView($userId, $noteIDToBeActive){
        $db = new DB_Functions();
        $dbNotes = $db->getNotesByUserID($userId);
        $notes = "";
        $array_notes = array();
        $counter = 1;
        foreach($dbNotes as $row => $innerArray){
            $array_notes[$innerArray["note_id"]] = $innerArray["note"];
            if($noteIDToBeActive == $innerArray["note_id"] ){
                $notes .= '<a href="#" class="list-group-item list-group-item-action active" id="'.$innerArray["note_id"].'" name="'.$innerArray["note"].'">'.'Note '.$counter.'</a>';
            }else if($noteIDToBeActive == -1 && $counter == 1){
                $notes .= '<a href="#" class="list-group-item list-group-item-action active" id="'.$innerArray["note_id"].'" name="'.$innerArray["note"].'">'.'Note '.$counter.'</a>';
            }else {
                $notes .= '<a href="#" class="list-group-item list-group-item-action" id="'.$innerArray["note_id"].'" name="'.$innerArray["note"].'">'.'Note '.$counter.'</a>';
            }
            $counter++;
        }
        return $notes;
    }
    
	/**
     * Get note and it's id by use id
     */
    public function getNotesByUserID($userId) {
        $stms = $this->conn->prepare("SELECT `note_id`, `note_title`, `note` FROM `notes` WHERE user_id = ?");
        $stms->bind_param("i", $userId);
        if ($stms->execute()) {
			$result = $stms->get_result();
			while ($data = $result->fetch_assoc()) {
				$notes[] = $data;
			}
            $stms->close(); 
			return $notes;
         } 
    }

    /**
     * update note in database by note id //UPDATE `notes` SET `note`= "ascsa", `note_title` = "ascas" WHERE `note_id` = 5 LIMIT 1
     */
    public function updateNoteByNoteID($id, $newNote, $newNoteTitle) {
        $stms = $this->conn->prepare("UPDATE `notes` SET `note`= ?, `note_title` = ? WHERE `note_id` = ? LIMIT 1");
        $stms->bind_param("ssi", $newNote, $newNoteTitle, $id);
        $stms->execute();
        $stms->close();
    }

    /**
     * delet note by user id
     */
    public function deleteNoteByUserId($id) {
        $stms = $this->conn->prepare("DELETE FROM `notes` WHERE `user_id` = ?");
        $stms->bind_param("i", $id);
        $stms->execute();
        $stms->close();
    }

    /**
     * add note in database by user id
     * and return note id by group all note by user id and sort it in descending oreder and select the first one
     */
    public function addNoteByUserId($userId) {
        //INSERT INTO `notes`(`user_id`, `note`) VALUES (3, "smnsknk")
        $temp = "{\\rtf1\\ansi\\ansicpg1252\\deff0\\deflang1033{\\fonttbl{\\f0\\fnil\\fcharset0 Microsoft Sans Serif;}}\\viewkind4\\uc1\\pard\\f0\\fs17 New Note\\par}";
        $temp = str_replace("\\", '\\\\', $temp);

        $stms = $this->conn->prepare("INSERT INTO `notes`(`user_id`, `note_title`, `note`) VALUES (?, 'new Note', ?)");
        $stms->bind_param("is", $userId, $temp);
        $stms->execute();
        $stms->close();

        //SELECT `note_id` FROM `notes` WHERE `user_id` = 3 ORDER BY `note_id` DESC LIMIT 1
        /*$stmt = $this->conn->prepare("SELECT `note_id` FROM `notes` WHERE `user_id` = ? ORDER BY `note_id` DESC LIMIT 1");
        $stmt->bind_param("i", $userId);
        $stmt->execute();
        $note = $stmt->get_result()->fetch_assoc();
        $stmt->close();

        return $note["note_id"];*/

    }

    public function saveJSON($json) {
        //INSERT INTO `notes`(`user_id`, `note`) VALUES (3, "smnsknk")
        $stms = $this->conn->prepare("INSERT INTO `temp`(`json`) VALUES (?)");
        $stms->bind_param("s", $json);
        $stms->execute();
        $stms->close();

        //SELECT `note_id` FROM `notes` WHERE `user_id` = 3 ORDER BY `note_id` DESC LIMIT 1
        /*$stmt = $this->conn->prepare("SELECT `note_id` FROM `notes` WHERE `user_id` = ? ORDER BY `note_id` DESC LIMIT 1");
        $stmt->bind_param("i", $userId);
        $stmt->execute();
        $note = $stmt->get_result()->fetch_assoc();
        $stmt->close();

        return $note["note_id"];*/

    }
 
    /**
     * Encrypting password
     * @param password
     * returns salt and encrypted password
     */
    public function hashSSHA($password) {
 
        $salt = sha1(rand());
        $salt = substr($salt, 0, 10);
        $encrypted = base64_encode(sha1($password . $salt, true) . $salt);
        $hash = array("salt" => $salt, "encrypted" => $encrypted);
        return $hash;
    }
 
    /**
     * Decrypting password
     * @param salt, password
     * returns hash string
     */
    public function checkhashSSHA($salt, $password) {
 
        $hash = base64_encode(sha1($password . $salt, true) . $salt);
 
        return $hash;
    }
 
}
 
?>