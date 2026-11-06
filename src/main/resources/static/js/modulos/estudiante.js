document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('estudianteModal');
    if (!modal) {
        return;
    }
    modal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        const editar = button.getAttribute('data-modo') === 'editar';
        document.getElementById('estudianteModalTitulo').textContent = button.getAttribute('data-titulo');
        document.getElementById('estudianteId').value = button.getAttribute('data-id') || '';
        document.getElementById('estudianteNombre').value = button.getAttribute('data-nombre') || '';
        document.getElementById('estudianteCorreo').value = button.getAttribute('data-correo') || '';
        document.getElementById('estudianteContrasena').value = '';
        document.getElementById('estudianteContrasena').required = !editar;
        document.getElementById('estudianteActivo').value = button.getAttribute('data-activo') === 'false' ? 'false' : 'true';
    });
});
