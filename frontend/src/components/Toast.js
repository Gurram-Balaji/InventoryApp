import { toast } from "react-toastify";

function errorToast(Message) {
	toast.error(Message, {
	  autoClose: 3000,
	});
  }
  
  function successToast(Message) {
	toast.success(Message, { autoClose: 3000 });
  }

  export {successToast, errorToast};