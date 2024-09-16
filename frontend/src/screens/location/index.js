import { useState, useEffect } from 'react';
import { Button, TextField, TablePagination } from '@mui/material';
import MotionHoc from "../MotionHoc";
import apiClient from '../../components/baseUrl';
import { errorToast } from '../../components/Toast';
import PlaylistAddIcon from '@mui/icons-material/PlaylistAdd';
import Delivery from '../assets/delivery.png';
import Shipping from '../assets/shipping.png';
import Pickup from '../assets/pickup.png';
import NewForm from './AddNewLocation';
import EditForm from './EditFormLocation';
import Delete from './DeleteLocation';
import TableLocation from './TableLocation';
import FulfillmentInfo from '../../components/FulfillmentInfo';


// Fetch paginated data
async function getData(page = 0, search = '') {
  try {
    const response = await apiClient.get(`/locations?page=${page}&search=${search}`);
    if (response.data.status === 404) {
      errorToast(response.data.message);
      return { content: [], page: { totalElements: 0 } }; // Return default on final failure
    }
    else if (response.data.success === true)
      return response.data.payload;
  } catch (error) {
    errorToast(error);
    return { content: [], page: { totalElements: 0 } }; // Return default on final failure
  }
}

function createData(id, locationId, locationDesc, locationType, addressLine1, addressLine2, addressLine3, city, state, country, pinCode, pickupAllowed, shippingAllowed, deliveryAllowed) {
  let fulfillment = [];

  const AddressLines = `${addressLine1 || ''} ${addressLine2 || ''} ${addressLine3 || ''} ${city || ''} ${state || ''} ${country || ''} ${pinCode || ''}`.trim();

  if (pickupAllowed) {
    fulfillment.push(<img key={`${id}-pickup`} src={Pickup} alt="Pickup Allowed" width='40' height='40' style={{ marginRight: '10px' }} />);
  }
  if (shippingAllowed) {
    fulfillment.push(<img key={`${id}-shipping`} src={Shipping} alt="Shipping Allowed" width='40' height='40' style={{ marginRight: '10px' }} />);
  }
  if (deliveryAllowed) {
    fulfillment.push(<img key={`${id}-delivery`} src={Delivery} alt="Delivery Allowed" width='40' height='40' />);
  }

  return {
    id, locationId, locationDesc, locationType, AddressLines, fulfillment, addressLine1, addressLine2, addressLine3, city, state, country, pinCode, pickupAllowed, shippingAllowed, deliveryAllowed
  };
}


const locationTypeOptions = [
  'WAREHOUSE',            // A storage facility for goods before they are distributed.
  'DISTRIBUTION_CENTER',  // A facility designed to receive, store, and ship products efficiently.
  'RETAIL_STORE',         // A physical store where goods are sold directly to consumers.
  'MANUFACTURING_PLANT',  // A location where raw materials are turned into finished products.
  'FULFILLMENT_CENTER',   // A facility focused on processing and fulfilling customer orders.
  'COLD_STORAGE',         // A temperature-controlled facility for storing perishable goods.
  'HUB',                  // A central location in a distribution or logistics network.
  'SHOWROOM',             // A location where products are displayed for viewing.
  'SUPPLIER_LOCATION'     // A location where raw materials or components are sourced from.
];


// The main component
const ReactVirtualizedTable = () => {
  const [page, setPage] = useState(0);
  const [total, setTotal] = useState(0);
  const [openEditDialog, setOpenEditDialog] = useState(false);
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [selectedRow, setSelectedRow] = useState(null);
  const [editData, setEditData] = useState({});
  const [searchQuery, setSearchQuery] = useState('');
  const [openAddDialog, setOpenAddDialog] = useState(false);
  const [location, setLocation] = useState([]);
  const [loading, setLoading] = useState(false);


  // Fetch paginated items
  const fetchRow = async (page, search) => {
    setLoading(true);
    const { content, page: { totalElements } } = await getData(page, search);
    const formattedRow = content.map(location => createData(location.id, location.locationId, location.locationDesc, location.locationType, location.addressLine1, location.addressLine2, location.addressLine3, location.city, location.state, location.country, location.pinCode, location.pickupAllowed, location.shippingAllowed, location.deliveryAllowed));
    setLocation(formattedRow);
    setTotal(totalElements);
    setLoading(false);
  };

  useEffect(() => {
    fetchRow(page, searchQuery);
  }, [page, searchQuery]);


  const handleEditOpen = (row) => {
    setEditData(row);
    setOpenEditDialog(true);
  };

  const handleDeleteOpen = (row) => {
    setSelectedRow(row);
    setOpenDeleteDialog(true);
  };

  const handleAddOpen = () => {
    setOpenAddDialog(true);
  };


  const handlePageChange = (_event, newPage) => {
    setPage(newPage);
  };

  const handleSearchChange = (e) => {
    setSearchQuery(e.target.value);
    setPage(0);

  };

  return (
    <>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0px 15px' }}>
        <h1 style={{ margin: 0 }}>Locations</h1>
        <TextField
          label="Search Location"
          value={searchQuery}
          onChange={handleSearchChange}
          variant="outlined"
          style={{ width: '50%', color: '#803bec' }}
        />
        <Button variant="contained" style={{ margin: '10px', background: '#803bec' }} onClick={handleAddOpen}>
          <PlaylistAddIcon /> Add Location
        </Button>
      </div>

      {/* Item Table Dialog */}
      <TableLocation handleEditOpen={handleEditOpen} handleDeleteOpen={handleDeleteOpen} loading={loading} location={location} />

      <TablePagination
        component="div"
        count={total}
        page={page}
        onPageChange={handlePageChange}
        rowsPerPage={8}
        rowsPerPageOptions={[8]}
      />

      {/* Add new item Dialog */}
      <NewForm locationTypeOptions={locationTypeOptions} fetchRow={fetchRow} page={page} setOpenAddDialog={setOpenAddDialog} openAddDialog={openAddDialog} />

      {/* Edit Dialog: Make Item ID non-editable */}
      <EditForm openEditDialog={openEditDialog} setOpenEditDialog={setOpenEditDialog} locationTypeOptions={locationTypeOptions} fetchRow={fetchRow} page={page} setEditData={setEditData} editData={editData} />

      {/* Delete Confirmation Dialog */}
      <Delete openDeleteDialog={openDeleteDialog} selectedRow={selectedRow} fetchRow={fetchRow} page={page} setOpenDeleteDialog={setOpenDeleteDialog} />

     <FulfillmentInfo/>

    </>
  );
};

export default MotionHoc(ReactVirtualizedTable);
