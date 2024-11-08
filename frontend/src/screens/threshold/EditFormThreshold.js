
import { errorToast, successToast } from '../../components/Toast';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from '@mui/material';
import apiClient from '../../components/baseUrl';

export default function EditFormThreshold({ openEditDialog, setOpenEditDialog, fetchRow, page, setEditData, editData }) {


  const handleEditSave = async () => {
    try {
      const { minThreshold, maxThreshold } = editData;

      if (!minThreshold || !maxThreshold) {
        errorToast("Please fill in all required fields.");
        return;
      }
      
      const response = await apiClient.patch(`/atpThresholds/${editData.thresholdId}`, editData);
      if (response.data.status === 404)
        errorToast(response.data.message);
      else if (response.data.success ) {
        successToast("Threshold updated successfully!");
        fetchRow(page);
      }
    } catch (error) {
      errorToast("Failed to update threshold");
    }
    setOpenEditDialog(false);
  };

  return (
     <Dialog PaperProps={{className: 'dialog-custom',  }} open={openEditDialog} maxWidth="md" fullWidth>
      <DialogTitle className="dialog-title-custom" >Edit Threshold...</DialogTitle>
      <DialogContent style={{ padding: '30px 50px 10px' }}>

        <TextField
          label="Item"
          fullWidth
          margin="normal"
          value={editData.itemId || ''}
          InputProps={{
            readOnly: true,
            style: {
              pointerEvents: 'none', // Prevent any interaction
            },
          }}
        />

        <TextField
          label="Location"
          fullWidth
          margin="normal"
          
          value={editData.locationId || ''}
          InputProps={{
            readOnly: true,
            style: {
              pointerEvents: 'none', // Prevent any interaction
            },
          }}
        />

        <TextField
          label="Min Threshold"
          type="number"
          fullWidth
          margin="normal"
          
          value={editData.minThreshold || ''}
          onChange={(e) => setEditData({ ...editData, minThreshold: e.target.value })}
        />

        <TextField
          label="Max Threshold"
          type="number"
          fullWidth
          margin="normal"
          
          value={editData.maxThreshold || ''}
          onChange={(e) => setEditData({ ...editData, maxThreshold: e.target.value })}
        />
      </DialogContent>
      <DialogActions style={{ display: 'flex', justifyContent: 'center', padding: '30px' }}>
        <Button
          onClick={() => setOpenEditDialog(false)}
          style={{
            backgroundColor: 'blue',
            color: 'white',
            fontWeight: 'bold',
            textTransform: 'uppercase',
            marginRight: '100px',
            padding: '10px 20px'
          }}
        >
          Cancel
        </Button>
        <Button
          onClick={handleEditSave}
          style={{
            backgroundColor: 'green',
            color: 'white',
            fontWeight: 'bold',
            textTransform: 'uppercase',
            padding: '10px 20px'
          }}
        >
          Save
        </Button>
      </DialogActions>
    </Dialog>
  );
}
