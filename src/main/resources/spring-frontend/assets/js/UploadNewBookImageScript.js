$('#uploadButton').on('click', function () {
    $('#dialog').trigger('click');
});

$('#dialog').on('change',function (){
    $('#imgForm').submit();
});