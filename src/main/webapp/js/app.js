/* ==========================================================================
   CMSC — Shared frontend behaviour (vanilla JS, no frameworks)
   Include this on every page: <script src="js/app.js"></script>
   ========================================================================== */

document.addEventListener('DOMContentLoaded', function () {
    initDarkMode();
    initSidebarToggle();
    initProfileDropdown();
    initBackToTop();
    initRipple();
    initTableSearchAndSort();
    initConfirmModals();
    initAutoToastFromAlerts();
});

/* ---------- Dark mode (persisted via localStorage) ---------- */
function initDarkMode() {
    const toggle = document.getElementById('darkModeToggle');
    const saved = localStorage.getItem('cmsc-theme');
    if (saved === 'dark') {
        document.documentElement.setAttribute('data-theme', 'dark');
    }
    if (!toggle) return;
    updateToggleIcon(toggle);
    toggle.addEventListener('click', function () {
        const isDark = document.documentElement.getAttribute('data-theme') === 'dark';
        if (isDark) {
            document.documentElement.removeAttribute('data-theme');
            localStorage.setItem('cmsc-theme', 'light');
        } else {
            document.documentElement.setAttribute('data-theme', 'dark');
            localStorage.setItem('cmsc-theme', 'dark');
        }
        updateToggleIcon(toggle);
    });
}
function updateToggleIcon(toggle) {
    const isDark = document.documentElement.getAttribute('data-theme') === 'dark';
    toggle.innerHTML = isDark ? '<i class="fa-solid fa-sun"></i>' : '<i class="fa-solid fa-moon"></i>';
}

/* ---------- Sidebar collapse (mobile) ---------- */
function initSidebarToggle() {
    const btn = document.querySelector('.sidebar-toggle');
    const sidebar = document.querySelector('.app-sidebar');
    if (!btn || !sidebar) return;
    btn.addEventListener('click', function () {
        sidebar.classList.toggle('open');
    });
    document.addEventListener('click', function (e) {
        if (window.innerWidth <= 900 && sidebar.classList.contains('open')
            && !sidebar.contains(e.target) && !btn.contains(e.target)) {
            sidebar.classList.remove('open');
        }
    });
}

/* ---------- Profile dropdown ---------- */
function initProfileDropdown() {
    const dropdown = document.querySelector('.profile-dropdown');
    if (!dropdown) return;
    const trigger = dropdown.querySelector('.profile-trigger');
    trigger.addEventListener('click', function (e) {
        e.stopPropagation();
        dropdown.classList.toggle('open');
    });
    document.addEventListener('click', function () {
        dropdown.classList.remove('open');
    });
}

/* ---------- Back to top ---------- */
function initBackToTop() {
    const btn = document.getElementById('backToTop');
    if (!btn) return;
    window.addEventListener('scroll', function () {
        btn.classList.toggle('show', window.scrollY > 300);
    });
    btn.addEventListener('click', function () {
        window.scrollTo({ top: 0, behavior: 'smooth' });
    });
}

/* ---------- Button ripple effect ---------- */
function initRipple() {
    document.querySelectorAll('.btn').forEach(function (btn) {
        btn.addEventListener('click', function (e) {
            const circle = document.createElement('span');
            const diameter = Math.max(btn.clientWidth, btn.clientHeight);
            const rect = btn.getBoundingClientRect();
            circle.style.width = circle.style.height = diameter + 'px';
            circle.style.left = (e.clientX - rect.left - diameter / 2) + 'px';
            circle.style.top = (e.clientY - rect.top - diameter / 2) + 'px';
            circle.classList.add('ripple');
            const old = btn.querySelector('.ripple');
            if (old) old.remove();
            btn.appendChild(circle);
            setTimeout(function () { circle.remove(); }, 500);
        });
    });
}

/* ---------- Table search + column sort (client-side, no data changes) ---------- */
function initTableSearchAndSort() {
    document.querySelectorAll('[data-searchable-table]').forEach(function (wrapper) {
        const input = wrapper.parentElement.querySelector('.table-search input');
        const table = wrapper.querySelector('table.data-table');
        if (!table) return;

        if (input) {
            input.addEventListener('input', function () {
                const term = input.value.toLowerCase();
                table.querySelectorAll('tbody tr').forEach(function (row) {
                    row.style.display = row.textContent.toLowerCase().includes(term) ? '' : 'none';
                });
            });
        }

        table.querySelectorAll('thead th[data-sort]').forEach(function (th, index) {
            th.addEventListener('click', function () {
                const tbody = table.querySelector('tbody');
                const rows = Array.from(tbody.querySelectorAll('tr'));
                const asc = th.getAttribute('data-order') !== 'asc';
                rows.sort(function (a, b) {
                    const aText = a.children[index].textContent.trim();
                    const bText = b.children[index].textContent.trim();
                    const aNum = parseFloat(aText), bNum = parseFloat(bText);
                    if (!isNaN(aNum) && !isNaN(bNum)) return asc ? aNum - bNum : bNum - aNum;
                    return asc ? aText.localeCompare(bText) : bText.localeCompare(aText);
                });
                rows.forEach(function (row) { tbody.appendChild(row); });
                table.querySelectorAll('thead th').forEach(function (h) { h.removeAttribute('data-order'); });
                th.setAttribute('data-order', asc ? 'asc' : 'desc');
            });
        });
    });
}

/* ---------- Confirmation modal before delete ----------
   Usage: <button class="btn btn-danger btn-sm" data-confirm-form="theFormId"
                   data-confirm-message="Delete this item?">
   The target <form> is only submitted after the user confirms.
*/
function initConfirmModals() {
    let pendingForm = null;
    const overlay = document.getElementById('confirmModal');
    if (!overlay) return;
    const messageEl = overlay.querySelector('.confirm-message');
    const yesBtn = overlay.querySelector('.confirm-yes');
    const noBtn = overlay.querySelector('.confirm-no');

    document.querySelectorAll('[data-confirm-form]').forEach(function (btn) {
        btn.addEventListener('click', function (e) {
            e.preventDefault();
            const formId = btn.getAttribute('data-confirm-form');
            pendingForm = document.getElementById(formId);
            messageEl.textContent = btn.getAttribute('data-confirm-message') || 'Are you sure?';
            overlay.classList.add('open');
        });
    });
    yesBtn.addEventListener('click', function () {
        overlay.classList.remove('open');
        if (pendingForm) pendingForm.submit();
    });
    noBtn.addEventListener('click', function () {
        overlay.classList.remove('open');
        pendingForm = null;
    });
}

/* ---------- Toast notifications ---------- */
function showToast(message, type) {
    let container = document.querySelector('.toast-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }
    const toast = document.createElement('div');
    toast.className = 'toast toast-' + (type || 'info');
    const icon = type === 'success' ? 'fa-circle-check' : type === 'error' ? 'fa-circle-exclamation' : 'fa-circle-info';
    toast.innerHTML = '<i class="fa-solid ' + icon + '"></i><span>' + message + '</span>' +
        '<button class="toast-close">&times;</button>';
    container.appendChild(toast);
    toast.querySelector('.toast-close').addEventListener('click', function () { toast.remove(); });
    setTimeout(function () { toast.remove(); }, 5000);
}

/* Converts existing .alert boxes (already rendered server-side from
   errorMessage/successMessage) into toasts as well, so both the inline
   alert and a toast confirm the same message — remove this call in
   initAutoToastFromAlerts() if you only want the inline alert box. */
function initAutoToastFromAlerts() {
    document.querySelectorAll('.alert-success').forEach(function (el) {
        showToast(el.textContent.trim(), 'success');
    });
    document.querySelectorAll('.alert-error').forEach(function (el) {
        showToast(el.textContent.trim(), 'error');
    });
}

/* ---------- Loading overlay for slow form submits ---------- */
function showLoadingOverlay() {
    const overlay = document.getElementById('loadingOverlay');
    if (overlay) overlay.classList.add('open');
}
document.querySelectorAll('form').forEach(function (form) {
    form.addEventListener('submit', function () { showLoadingOverlay(); });
});