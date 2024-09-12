import { errorToast, successToast } from '../../components/Toast';
import { Button, Dialog, DialogActions, DialogContent, DialogTitle, TextField, Checkbox, FormControl, FormControlLabel, InputLabel, MenuItem, Select } from '@mui/material';
import apiClient from '../../components/baseUrl';


export default function EditFormItem({ openEditDialog, editData, statusOptions, fetchItems, page, setOpenEditDialog, setEditData }) {
  const handleEditSave = async () => {
    try {
      const { itemid, itemDescription, category, type, price } = editData;
      if (!itemid || !itemDescription || !category || !type || !price) {
        errorToast("Please fill in all required fields.");
        return;
      }

      if (price < 0) {
        errorToast("Price Should not be negitive.");
        return;
    }
    
      const response = await apiClient.patch(`/items/${editData.itemid}`, editData);
      if (response.data.status === 404)
        errorToast(response.data.message);
      else if (response.data.success === true) {
        successToast("Item updated successfully!");
        fetchItems(page); // Refresh items after delete
      }
    } catch (error) {
      errorToast("Failed to update item");
    }
    setOpenEditDialog(false);
  };

  return (
    <Dialog open={openEditDialog} onClose={() => setOpenEditDialog(false)} maxWidth="md" fullWidth>
      <DialogTitle>Edit Item...</DialogTitle>
      <DialogContent style={{ padding: '30px 50px 10px' }}>
        {editData && (
          <TextField
            label="Item ID"
            fullWidth
            margin="normal"
            variant="standard"
            value={editData.itemid || ''}
            InputProps={{
              readOnly: true,
            }}
          />
        )}
        <TextField
          label="Description"
          fullWidth
          margin="normal"
          variant="standard"
          value={editData.itemDescription || ''}
          onChange={(e) => setEditData({ ...editData, itemDescription: e.target.value })}
        />
        <TextField
          label="Category"
          fullWidth
          margin="normal"
          variant="standard"
          value={editData.category || ''}
          onChange={(e) => setEditData({ ...editData, category: e.target.value })}
        />
        <TextField
          label="Type"
          fullWidth
          margin="normal"
          variant="standard"
          value={editData.type || ''}
          onChange={(e) => setEditData({ ...editData, type: e.target.value })}
        />
        <FormControl fullWidth margin="dense">
          <InputLabel>Status</InputLabel>
          <Select
            value={editData.status || ''}
            onChange={(e) => setEditData({ ...editData, status: e.target.value })}
            variant="standard"
          >
            {statusOptions.map((status) => (
              <MenuItem key={status} value={status}>
                {status}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
        <TextField
          label="Price"
          fullWidth
          margin="normal"
          variant="standard"
          type="number"
          value={editData.price || ''}
          onChange={(e) => setEditData({ ...editData, price: e.target.value })}
        />
        <FormControlLabel
          control={
            <Checkbox
              checked={editData.pickupAllowed || false}
              onChange={(e) => setEditData({ ...editData, pickupAllowed: e.target.checked })}
            />
          }
          label="Pickup Allowed"
        />
        <FormControlLabel
          control={
            <Checkbox
              checked={editData.shippingAllowed || false}
              onChange={(e) => setEditData({ ...editData, shippingAllowed: e.target.checked })}
            />
          }
          label="Shipping Allowed"
        />
        <FormControlLabel
          control={
            <Checkbox
              checked={editData.deliveryAllowed || false}
              onChange={(e) => setEditData({ ...editData, deliveryAllowed: e.target.checked })}
            />
          }
          label="Delivery Allowed"
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
  )
}