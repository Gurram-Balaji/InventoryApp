import React, { useState, useEffect } from 'react';
import { Button, TextField, TablePagination, Select, MenuItem, FormControl, InputLabel } from '@mui/material';
import MotionHoc from "../MotionHoc";
import apiClient from '../../components/baseUrl';
import { errorToast } from '../../components/Toast';
import PlaylistAddIcon from '@mui/icons-material/PlaylistAdd';
import NewForm from './AddNewSupply';
import EditForm from './EditFormSupply';
import Delete from './DeleteSupply';
import TableSupply from './TableSupply';



// Fetch paginated data
async function getData(page = 0, search = '', searchBy = '') {
  try {
    const response = await apiClient.get(`/supply/all?page=${page}&search=${search}&searchBy=${searchBy}`);
    if (response.data.status === 404) {
      errorToast(response.data.message);
      return { content: [], page: { totalElements: 0 } }; // Return default on final failure

    }
    else if (response.data.success )
      return response.data.payload;
  } catch (error) {
    errorToast(error);
    return { content: [], page: { totalElements: 0 } }; // Return default on final failure
  }
}

function createData(supplyId, itemId, locationId, supplyType, quantity) {
  return {
    supplyId, itemId, locationId, supplyType, quantity
  };
}
const supplyOptions = [
  'ONHAND',
  'INTRANSIT',
  'DAMAGED'
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
  const [supply, setSupply] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchBy, setSearchBy] = useState('item');

  // Fetch paginated items
  const fetchRow = async (page, search, searchBy) => {
    setLoading(true);
    const { content, page: { totalElements } } = await getData(page, search, searchBy);
    console.error(content);
    const formattedRow = content.map(supply => createData(supply.supplyId, supply.itemDescription + ' (' + supply.itemId + ')', supply.locationDescription
      + ' (' + supply.locationId + ')', supply.supplyType, supply.quantity));
    setSupply(formattedRow);
    setTotal(totalElements);
    setLoading(false);
  };

  useEffect(() => {
    fetchRow(page, searchQuery, searchBy);
  }, [page, searchQuery, searchBy]);


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
    const value = e.target.value;
  
    // Regular expression to allow only numbers and alphabets
    const regex = /^[a-zA-Z0-9\s]*$/;
  
    // Check if the value contains any special characters
    if (!regex.test(value)) {
      errorToast("Error: Search query contains special characters!");
      return;
    }
  
    setSearchQuery(value);
    setPage(0);
  };

  const handleSelectChange = (event) => {
    setSearchBy(event.target.value);
  };

  return (
    <>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0px 15px' }}>
        <h1 style={{ margin: 0 }}>Supply</h1>
        <div style={{ display: 'flex', alignItems: 'center', width: "60%" }}>
          <TextField
            label="Search Supply"
            value={searchQuery}
            onChange={handleSearchChange}
            variant="outlined"
            style={{ flexGrow: 1, color: '#803bec', marginRight: '10px' }}
          />
          <FormControl variant="outlined" style={{ marginRight: '10px', minWidth: '170px' }}>
            <InputLabel id="search-by-label">Search By</InputLabel>
            <Select
              labelId="search-by-label"
              value={searchBy}
              onChange={handleSelectChange}
              label="Search By"
            >
              <MenuItem value="item">Item</MenuItem>
              <MenuItem value="location">Location</MenuItem>
              <MenuItem value="supplyType">Supply Type</MenuItem>
            </Select>
          </FormControl>
        </div>
        <Button variant="contained" style={{ margin: '10px', background: '#803bec' }} onClick={handleAddOpen}>
          <PlaylistAddIcon /> Add Supply
        </Button>
      </div>

      {/* Item Table Dialog */}
      <TableSupply handleEditOpen={handleEditOpen} handleDeleteOpen={handleDeleteOpen} loading={loading} supply={supply} />

      <TablePagination
        component="div"
        count={total}
        page={page}
        onPageChange={handlePageChange}
        rowsPerPage={8}
        rowsPerPageOptions={[8]}
      />

      {/* Add new item Dialog */}
      <NewForm supplyOptions={supplyOptions} fetchRow={fetchRow} page={page} setOpenAddDialog={setOpenAddDialog} openAddDialog={openAddDialog} />

      {/* Edit Dialog: Make Item ID non-editable */}
      <EditForm openEditDialog={openEditDialog} setOpenEditDialog={setOpenEditDialog} supplyOptions={supplyOptions} fetchRow={fetchRow} page={page} setEditData={setEditData} editData={editData} />

      {/* Delete Confirmation Dialog */}
      <Delete openDeleteDialog={openDeleteDialog} selectedRow={selectedRow} fetchRow={fetchRow} page={page} setOpenDeleteDialog={setOpenDeleteDialog} />

    </>
  );
};

export default MotionHoc(ReactVirtualizedTable);
