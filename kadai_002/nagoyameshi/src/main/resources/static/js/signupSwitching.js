//  id="viewFirst"を取得
let viewiconFirst = document.getElementById('viewFirst');

//  id="viewSecond"を取得
let viewiconSecond = document.getElementById('viewSecond');

//  id="password"を取得
let inputtypeFirst = document.getElementById('password');

//  id="passwordConfirmation"を取得
let inputtypeSecond = document.getElementById('passwordConfirmation');

//  id="viewFirst"クリック時の処理を設定
viewiconFirst.addEventListener('click', () => {

       //  passwordからtextへ
       if(inputtypeFirst.type === 'password'){
              inputtypeFirst.type = 'text';
              viewiconFirst.innerHTML = '<i class="bi bi-eye-fill"></i>';

        //  textからpasswordへ
        } else {
               inputtypeFirst.type = 'password';
               viewiconFirst.innerHTML = '<i class="bi bi-eye-slash-fill"></i>';
        }
});

//  id="viewSecond"クリック時の処理を設定
viewiconSecond.addEventListener('click', () => {

       //  passwordからtextへ
       if(inputtypeSecond.type === 'password'){
              inputtypeSecond.type = 'text';
              viewiconSecond.innerHTML = '<i class="bi bi-eye-fill"></i>';

        //  textからpasswordへ
        } else {
               inputtypeSecond.type = 'password';
               viewiconSecond.innerHTML = '<i class="bi bi-eye-slash-fill"></i>';
        }
});
