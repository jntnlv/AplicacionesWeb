document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('tutorModal');
    if (!modal) {
        return;
    }
    modal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        const editar = button.getAttribute('data-modo') === 'editar';
        const ids = (button.getAttribute('data-materias') || '').split(',').filter(Boolean);
        document.getElementById('tutorModalTitulo').textContent = button.getAttribute('data-titulo');
        document.getElementById('tutorId').value = button.getAttribute('data-id') || '';
        document.getElementById('tutorNombre').value = button.getAttribute('data-nombre') || '';
        document.getElementById('tutorCorreo').value = button.getAttribute('data-correo') || '';
        document.getElementById('tutorContrasena').value = '';
        document.getElementById('tutorContrasena').required = !editar;
        document.querySelectorAll('.materia-check').forEach(function (check) {
            check.checked = ids.includes(check.value);
        });
    });
});
