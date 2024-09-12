

import { Button, Dialog, DialogActions, DialogContent, DialogTitle } from '@mui/material';
import apiClient from '../../components/baseUrl';
import { errorToast, successToast } from '../../components/Toast';

export default function Delete({ openDeleteDialog, selectedRow, fetchRow, page, setOpenDeleteDialog }) {
  const handleDeleteConfirm = async () => {
    try {
      const response = await apiClient.delete(`/atpThresholds/${selectedRow.thresholdId}`);
      if (response.data.status === 404)
        errorToast(response.data.message);
      else if (response.data.success === true) {
        successToast("Threshold deleted successfully!");
        fetchRow(page);
      }
    } catch (error) {
      errorToast("Failed to delete Threshold");
    }
    setOpenDeleteDialog(false);
  };

  return (<Dialog open={openDeleteDialog} onClose={() => setOpenDeleteDialog(false)} maxWidth="sm" fullWidth>
    <DialogTitle>Confirm Delete...</DialogTitle>
    <DialogContent>
      {selectedRow && (
        <div style={{
          textAlign: 'center',
          paddingTop: '10px',
          fontFamily: 'Arial, sans-serif',
        }}>
          <p style={{
            fontWeight: 'bold',
            fontSize: '18px',
          }}>
            {selectedRow.locationId}
          </p>
          <p style={{
            fontWeight: 'bold',
            fontSize: '18px',
            margin: '10px 0',
          }}>
            {selectedRow.locationDesc}
          </p>
          <p style={{
            fontWeight: 'bold',
            fontSize: '18px',
          }}>
            {selectedRow.itemId}
          </p>
          <p style={{
            fontWeight: 'bold',
            fontSize: '18px',
            margin: '10px 0',
          }}>
            {selectedRow.itemDescription}
          </p>

          <p style={{
            fontSize: '16px',
          }}>
            Are you sure you want to delete this threshold?
          </p>
        </div>
      )}
    </DialogContent>


    <DialogActions style={{ display: 'flex', justifyContent: 'center', padding: '26px' }}>
      <Button
        onClick={() => setOpenDeleteDialog(false)}
        style={{
          backgroundColor: 'blue',
          color: 'white',
          fontWeight: 'bold',
          textTransform: 'uppercase',
          marginRight: '100px', // Space between buttons
        }}
      >
        Cancel
      </Button>
      <Button
        onClick={handleDeleteConfirm}
        style={{
          backgroundColor: 'red',
          color: 'white',
          fontWeight: 'bold',
          textTransform: 'uppercase',
        }}
      >
        Delete
      </Button>
    </DialogActions>
  </Dialog>
  )
}