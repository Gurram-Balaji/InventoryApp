import React, { useState, useEffect } from 'react';
import { Button, TextField, TablePagination, Select, MenuItem, FormControl, InputLabel } from '@mui/material';
import MotionHoc from "../MotionHoc";
import apiClient from '../../components/baseUrl';
import { errorToast } from '../../components/Toast';
import PlaylistAddIcon from '@mui/icons-material/PlaylistAdd';
import NewForm from './AddNewDemand';
import EditForm from './EditFormDemand';
import Delete from './DeleteDemand';
import TableDemand from './TableDemand';


// Fetch paginated data
async function getData(page = 0, search = '', searchBy = '') {
  try {
    const response = await apiClient.get(`/demand/all?page=${page}&search=${search}&searchBy=${searchBy}`);
   
    if (response.data.status === 404) {
      errorToast(response.data.message);
      return { content: [], page: { totalElements: 0 } }
    }
    else if (response.data.success ) {
      return response.data.payload;
    }
  } catch (error) {
    errorToast(error);
    return { content: [], page: { totalElements: 0 } }; // Return default on final failure
  }
}

function createData(demandId, itemId, locationId, demandType, quantity) {
  return {
    demandId, itemId, locationId, demandType, quantity
  };
}
const demandOptions = [
  'HARD_PROMISED',
  'PLANNED'
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
  const [demand, setDemand] = useState([]);
  const [loading, setLoading] = useState(false);
  const [searchBy, setSearchBy] = useState('item');

  // Fetch paginated items
  const fetchRow = async (page, search, searchBy) => {
    setLoading(true);
    const { content, page: { totalElements } } = await getData(page, search, searchBy);
    const formattedRow = content.map(demand => createData(demand.demandId, demand.itemDescription + ' (' + demand.itemId + ')', demand.locationDescription
      + ' (' + demand.locationId + ')', demand.demandType, demand.quantity));
      setDemand(formattedRow);
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
    setSearchQuery(e.target.value);
    setPage(0);
  };

  const handleSelectChange = (event) => {
    setSearchBy(event.target.value);
  };


  return (
    <>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0px 15px' }}>
        <h1 style={{ margin: 0 }}>Demand</h1>
        <div style={{ display: 'flex', alignItems: 'center', width: "55%" }}>
          <TextField
            label="Search Demand"
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
              <MenuItem value="demandType">Demand Type</MenuItem>
            </Select>
          </FormControl>
        </div>
        <Button variant="contained" style={{ margin: '10px', background: '#803bec' }} onClick={handleAddOpen}>
          <PlaylistAddIcon /> Add Demand
        </Button>
      </div>

      {/* Item Table Dialog */}
      <TableDemand handleEditOpen={handleEditOpen} handleDeleteOpen={handleDeleteOpen} loading={loading} demand={demand} />

      <TablePagination
        component="div"
        count={total}
        page={page}
        onPageChange={handlePageChange}
        rowsPerPage={8}
        rowsPerPageOptions={[8]}
      />

      {/* Add new item Dialog */}
      <NewForm demandOptions={demandOptions} fetchRow={fetchRow} page={page} setOpenAddDialog={setOpenAddDialog} openAddDialog={openAddDialog} />

      {/* Edit Dialog: Make Item ID non-editable */}
      <EditForm openEditDialog={openEditDialog} setOpenEditDialog={setOpenEditDialog} demandOptions={demandOptions} fetchRow={fetchRow} page={page} setEditData={setEditData} editData={editData} />

      {/* Delete Confirmation Dialog */}
      <Delete openDeleteDialog={openDeleteDialog} selectedRow={selectedRow} fetchRow={fetchRow} page={page} setOpenDeleteDialog={setOpenDeleteDialog} />

    </>
  );
};

export default MotionHoc(ReactVirtualizedTable);
