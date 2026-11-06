document.addEventListener('DOMContentLoaded', function () {
    const modal = document.getElementById('materiaModal');
    if (!modal) {
        return;
    }
    modal.addEventListener('show.bs.modal', function (event) {
        const button = event.relatedTarget;
        document.getElementById('materiaModalTitulo').textContent = button.getAttribute('data-titulo');
        document.getElementById('materiaId').value = button.getAttribute('data-id') || '';
        document.getElementById('materiaNombre').value = button.getAttribute('data-nombre') || '';
    });
});
