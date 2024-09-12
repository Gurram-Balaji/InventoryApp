import { useState, useEffect } from 'react';
import { Button, TextField, TablePagination } from '@mui/material';
import MotionHoc from "../MotionHoc";
import apiClient from '../../components/baseUrl';
import { errorToast } from '../../components/Toast';
import PlaylistAddIcon from '@mui/icons-material/PlaylistAdd';
import NewForm from './AddNewThreshold';
import EditForm from './EditFormThreshold';
import Delete from './DeleteThreshold';
import TableThreshold from './TableThreshold';

// Fetch paginated data
async function getData(page = 0, search = '') {
  try {
    const response = await apiClient.get(`/atpThresholds/all?page=${page}&search=${search}`);
    if (response.data.status === 404) {
      errorToast(response.data.message);
      return { content: [], page: { totalElements: 0 } }
    }
    else if (response.data.success === true) {
      return response.data.payload;
    }
  } catch (error) {
      errorToast(error);
      return { content: [], page: { totalElements: 0 } }; // Return default on final failure
  }
}

function createData(thresholdId, itemId, locationId, minThreshold, maxThreshold) {
  return {
    thresholdId, itemId, locationId, minThreshold, maxThreshold
  };
}


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
  const [threshold, setThreshold] = useState([]);
  const [loading, setLoading] = useState(false);

  // Fetch paginated items
  const fetchRow = async (page, search) => {
    setLoading(true);
    const { content, page: { totalElements } } = await getData(page, search);
    const formattedRow = content.map(threshold => createData(threshold.thresholdId, threshold.itemDescription + ' (' + threshold.itemId + ')', threshold.locationDescription
      + ' (' + threshold.locationId + ')', threshold.minThreshold, threshold.maxThreshold));
    setThreshold(formattedRow);
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
        <h1 style={{ margin: 0 }}>Threshold</h1>
        <TextField
          label="Search Threshold"
          value={searchQuery}
          onChange={handleSearchChange}
          variant="outlined"
          style={{ width: '50%', color: '#803bec' }}
        />
        <Button variant="contained" style={{ margin: '10px', background: '#803bec' }} onClick={handleAddOpen}>
          <PlaylistAddIcon /> Add Threshold
        </Button>
      </div>

      {/* Item Table Dialog */}
      <TableThreshold handleEditOpen={handleEditOpen} handleDeleteOpen={handleDeleteOpen} loading={loading} threshold={threshold} />

      <TablePagination
        component="div"
        count={total}
        page={page}
        onPageChange={handlePageChange}
        rowsPerPage={8}
        rowsPerPageOptions={[8]}
      />

      {/* Add new item Dialog */}
      <NewForm fetchRow={fetchRow} page={page} setOpenAddDialog={setOpenAddDialog} openAddDialog={openAddDialog} />

      {/* Edit Dialog: Make Item ID non-editable */}
      <EditForm openEditDialog={openEditDialog} setOpenEditDialog={setOpenEditDialog} fetchRow={fetchRow} page={page} setEditData={setEditData} editData={editData} />

      {/* Delete Confirmation Dialog */}
      <Delete openDeleteDialog={openDeleteDialog} selectedRow={selectedRow} fetchRow={fetchRow} page={page} setOpenDeleteDialog={setOpenDeleteDialog} />

    </>
  );
};

export default MotionHoc(ReactVirtualizedTable);
