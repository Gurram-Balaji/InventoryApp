
import { errorToast, successToast } from '../../components/Toast';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField} from '@mui/material';
import apiClient from '../../components/baseUrl';

export default function EditFormDemand({ openEditDialog, setOpenEditDialog, fetchRow, page, setEditData, editData }) {


  const handleEditSave = async () => {
    try {
      const { itemId, locationId, demandType, quantity } = editData;

      if (!itemId || !locationId || !demandType || !quantity) {
        errorToast("Please fill in all required fields.");
        return;
      }

      if (quantity<0) {
        errorToast("Quantity should not be negitive.");
        return;
    }

      const response=await apiClient.patch(`/demand/${editData.demandId}`, editData);
      if (response.data.status === 404)
          errorToast(response.data.message);
        else if (response.data.success === true) {
          successToast("Demand updated successfully!");
          fetchRow(page); // Refresh items after delete
        }
    } catch (error) {
      errorToast("Failed to update demand");
    }
    setOpenEditDialog(false);
  };

  return (
     <Dialog PaperProps={{className: 'dialog-custom',  }}open={openEditDialog} onClose={() => setOpenEditDialog(false)} maxWidth="md" fullWidth>
      <DialogTitle className="dialog-title-custom" >Edit Demand...</DialogTitle>
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
          label="Demand Type"
          fullWidth
          margin="normal"
          
          value={editData.demandType || ''}
          InputProps={{
            readOnly: true,
            style: {
              pointerEvents: 'none', // Prevent any interaction
            },
          }}
        />

        {/* Quantity Field */}
        <TextField
          label="Quantity"
          type="number"
          fullWidth
          margin="normal"
          
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
