//package com.example.hotelcheckin
//
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.Query
//
//@Dao //DAO인터페이스 데이터베이스에 접근하고 쿼리를 실행한다
//interface Reservesave {
//    @Insert // 데이터 베이스에 삽입할거라구
//    suspend fun insert(reserv: ReserveList) //suspend -> 이게 붙으면 코루틴(비동기 디자인패턴)이 호출가능
//    //왜? 굳이 비동기패턴씀? 걍 값입력받고 호출해서 db에 박으면되는거아님?
//    //Room에서 db작업은 기본적으로 IO 작업이고 이는 시간이 오래걸림.
//    //사용자가 값을 다입력했는데 main ui도 안바뀌고 작업이 끝날때까지 가만히있으면 사용자 입장에선 문제가 생겨서 응답을 안하는건지 알수없고 기다리게됨
//    //그래서 비동기 패턴으로 사용자 할거 하고 그동안 db에 삽입해 넣는거
//    @Query("SELECT * FROM reservation") // 쿼리(DB)를 호출할거야 SELECT선택한대 * FROM 어디로부터? reservation DB의 Entity테이블 모든걸 가져올거다
//    suspend fun getAllReservation(): List<ReserveList> //리스트로 받는다
//}
