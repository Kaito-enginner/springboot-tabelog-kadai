//  id="viewFirst"を取得
let viewicon = document.getElementById('view');

//  id="password"を取得
let inputtype = document.getElementById('password');

//  id="viewFirst"クリック時の処理を設定
viewicon.addEventListener('click', () => {

       //  passwordからtextへ
       if(inputtype.type === 'password'){
              inputtype.type = 'text';
              viewicon.innerHTML = '<i class="bi bi-eye-fill"></i>';

        //  textからpasswordへ
        } else {
               inputtype.type = 'password';
               viewicon.innerHTML = '<i class="bi bi-eye-slash-fill"></i>';
        }
});
