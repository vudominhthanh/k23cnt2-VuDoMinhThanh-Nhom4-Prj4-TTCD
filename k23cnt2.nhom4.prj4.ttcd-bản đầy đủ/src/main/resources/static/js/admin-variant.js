function openAddModal(){

    document.getElementById("modalTitle").innerText =
        "Thêm kích cỡ";

    document.getElementById("variantId").value = "";

    document.getElementById("productId").value = "";

    document.getElementById("sizeName").value = "";

    document.getElementById("extraPrice").value = "";

    document.getElementById("variantModal").style.display =
        "flex";
}

function closeModal(){

    document.getElementById("variantModal").style.display =
        "none";
}

function openEditModal(button){

    document.getElementById("modalTitle").innerText =
        "Sửa kích cỡ";

    document.getElementById("variantId").value =
        button.dataset.id;

    document.getElementById("productId").value =
        button.dataset.product;

    document.getElementById("sizeName").value =
        button.dataset.size;

    document.getElementById("extraPrice").value =
        button.dataset.price;

    document.getElementById("variantModal").style.display =
        "flex";
}

async function saveVariant(){

    const id =
        document.getElementById("variantId").value;

    const formData = new FormData();

    formData.append(
        "productId",
        document.getElementById("productId").value
    );

    formData.append(
        "sizeName",
        document.getElementById("sizeName").value
    );

    formData.append(
        "extraPrice",
        document.getElementById("extraPrice").value
    );

    let url =
        "/admin/variants/save";

    if(id){

        url += "/" + id;
    }

    await fetch(url,{
        method:"POST",
        body:formData
    });

    location.reload();
}

async function deleteVariant(id){

    if(!confirm("Xóa kích cỡ này?")){
        return;
    }

    await fetch(
        "/api/admin/variants/" + id,
        {
            method:"DELETE"
        }
    );

    location.reload();
}