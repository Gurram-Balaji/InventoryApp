
import { errorToast, successToast } from '../../components/Toast';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from '@mui/material';
import apiClient from '../../components/baseUrl';

export default function EditFormSupply({ openEditDialog, setOpenEditDialog, fetchRow, page, setEditData, editData }) {


  const handleEditSave = async () => {
    try {
      const { itemId, locationId, supplyType, quantity } = editData;

      if (!itemId || !locationId || !supplyType || !quantity) {
        errorToast("Please fill in all required fields.");
        return;
      }

      if (quantity<0) {
        errorToast("Quantity should not be negitive.");
        return;
    }

      const response = await apiClient.patch(`/supply/${editData.supplyId}`, editData);

      if (response.data.status === 404)
        errorToast(response.data.message);
      else if (response.data.success === true) {
        successToast("Supply updated successfully!");
        fetchRow(page);
      }

    } catch (error) {
      errorToast("Failed to update supply");
    }
    setOpenEditDialog(false);
  };

  return (
    <Dialog open={openEditDialog} onClose={() => setOpenEditDialog(false)} maxWidth="md" fullWidth>
      <DialogTitle>Edit Supply...</DialogTitle>
      <DialogContent style={{ padding: '30px 50px 10px' }}>

        <TextField
          label="Item"
          fullWidth
          margin="normal"
          variant="standard"
          value={editData.itemId || ''}
          InputProps={{
            readOnly: true,
          }}
        />

        <TextField
          label="Location"
          fullWidth
          margin="normal"
          variant="standard"
          value={editData.locationId || ''}
          InputProps={{
            readOnly: true,
          }}
        />

        <TextField
          label="Supply Type"
          fullWidth
          margin="normal"
          variant="standard"
          value={editData.supplyType || ''}
          InputProps={{
            readOnly: true,
          }}
        />

        {/* Quantity Field */}
        <TextField
          label="Quantity"
          type="number"
          fullWidth
          margin="normal"
          variant="standard"
          value={editData.quantity || ''}
          onChange={(e) => setEditData({ ...editData, quantity: e.target.value })}
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
