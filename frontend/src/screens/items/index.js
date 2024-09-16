import { useState, useEffect } from 'react';
import { Button, TextField, TablePagination } from '@mui/material';
import MotionHoc from "../MotionHoc";
import apiClient from '../../components/baseUrl';
import { errorToast } from '../../components/Toast';
import PlaylistAddIcon from '@mui/icons-material/PlaylistAdd';
import Delivery from '../assets/delivery.png';
import Shipping from '../assets/shipping.png';
import Pickup from '../assets/pickup.png';
import NewItemForm from './AddNewItem';
import EditFormItem from './EditFormItem';
import DeleteItem from './DeleteItem';
import TableItem from './TableItem';
import FulfillmentInfo from '../../components/FulfillmentInfo';



// Fetch paginated data
async function getItemsData(page = 0, search = '') {
  try {
    const response = await apiClient.get(`/items?page=${page}&search=${search}`);
    if (response.data.status === 404) {
      errorToast(response.data.message);
      return { content: [], page: { totalElements: 0 } };
    }
    else if (response.data.success === true)
      return response.data.payload;
  } catch (error) {
    errorToast(error);
    return { content: [], page: { totalElements: 0 } }; // Return default on final failure
  }
}

function createData(id, itemid, itemDescription, category, type, price, pickupAllowed, shippingAllowed, deliveryAllowed, status) {
  let fulfillment = [];

  // Conditionally add images/icons based on the fulfillment options
  if (pickupAllowed) {
    fulfillment.push(<img key={`${id}-pickup`} src={Pickup} alt="Pickup Allowed" width='40' height='40' style={{ marginRight: '10px' }} />);
  }
  if (shippingAllowed) {
    fulfillment.push(<img key={`${id}-shipping`} src={Shipping} alt="Shipping Allowed" width='40' height='40' style={{ marginRight: '10px' }} />);
  }
  if (deliveryAllowed) {
    fulfillment.push(<img key={`${id}-delivery`} src={Delivery} alt="Delivery Allowed" width='40' height='40' />);
  }

  return { id, itemid, itemDescription, category, type, price, fulfillment, status, pickupAllowed, shippingAllowed, deliveryAllowed };
}

const statusOptions = [
  'ACTIVE',
  'INACTIVE',
  'DISCONTINUED',
];

// The main component
const ReactVirtualizedTable = () => {
  const [page, setPage] = useState(0);
  const [totalItems, setTotalItems] = useState(0);
  const [openEditDialog, setOpenEditDialog] = useState(false);
  const [openDeleteDialog, setOpenDeleteDialog] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);
  const [editData, setEditData] = useState({});
  const [searchQuery, setSearchQuery] = useState('');
  const [openAddDialog, setOpenAddDialog] = useState(false);
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);


  // Fetch paginated items
  const fetchItems = async (page, search) => {
    setLoading(true);
    const { content, page: { totalElements } } = await getItemsData(page, search);
    const formattedItems = content.map(item => createData(item.id, item.itemId, item.itemDescription, item.category, item.type, item.price, item.pickupAllowed, item.shippingAllowed, item.deliveryAllowed, item.status));
    setItems(formattedItems);
    setTotalItems(totalElements);
    setLoading(false);
  };

  useEffect(() => {
    fetchItems(page, searchQuery);
  }, [page, searchQuery]);


  const handleEditOpen = (item) => {
    setEditData(item);
    setOpenEditDialog(true);
  };

  const handleDeleteOpen = (item) => {
    setSelectedItem(item);
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
  };



  return (
    <>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0px 15px' }}>
        <h1 style={{ margin: 0 }}>Items</h1>
        <TextField
          label="Search Items"
          value={searchQuery}
          onChange={handleSearchChange}
          variant="outlined"
          style={{ width: '50%', color: '#803bec' }}
        />
        <Button variant="contained" style={{ margin: '10px', background: '#803bec' }} onClick={handleAddOpen}>
          <PlaylistAddIcon /> Add Item
        </Button>
      </div>

      {/* Item Table Dialog */}
      <TableItem handleEditOpen={handleEditOpen} handleDeleteOpen={handleDeleteOpen} loading={loading} items={items} />

      <TablePagination
        component="div"
        count={totalItems}
        page={page}
        onPageChange={handlePageChange}
        rowsPerPage={8}
        rowsPerPageOptions={[8]}
      />

      {/* Add new item Dialog */}
      <NewItemForm statusOptions={statusOptions} fetchItems={fetchItems} page={page} setOpenAddDialog={setOpenAddDialog} openAddDialog={openAddDialog} />

      {/* Edit Dialog: Make Item ID non-editable */}
      <EditFormItem openEditDialog={openEditDialog} setOpenEditDialog={setOpenEditDialog} statusOptions={statusOptions} fetchItems={fetchItems} page={page} setEditData={setEditData} editData={editData} />

      {/* Delete Confirmation Dialog */}
      <DeleteItem openDeleteDialog={openDeleteDialog} selectedItem={selectedItem} fetchItems={fetchItems} page={page} setOpenDeleteDialog={setOpenDeleteDialog} />
      <FulfillmentInfo />
    </>
  );
};

const Items = MotionHoc(ReactVirtualizedTable);
export default Items;
